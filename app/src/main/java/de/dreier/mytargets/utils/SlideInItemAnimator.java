package de.dreier.mytargets.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RecyclerView.ItemAnimator} that fades & slides newly added items in from a given
 * direction.
 */
public class SlideInItemAnimator extends DefaultItemAnimator {

    private final List<RecyclerView.ViewHolder> pendingAdds = new ArrayList<>();
    private final int slideFromEdge;
    private boolean useDefaultAnimator = false;

    /**
     * Default to sliding in upward.
     */
    public SlideInItemAnimator() {
        this(Gravity.BOTTOM, -1); // undefined layout dir; bottom isn't relative
    }

    public SlideInItemAnimator(int slideFromEdge, int layoutDirection) {
        this.slideFromEdge = Gravity.getAbsoluteGravity(slideFromEdge, layoutDirection);
        setAddDuration(160L);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        if (useDefaultAnimator) {
            return super.animateAdd(holder);
        }
        holder.itemView.setAlpha(0f);
        switch (slideFromEdge) {
            case Gravity.START:
                holder.itemView.setTranslationX(-holder.itemView.getWidth() / 3);
                break;
            case Gravity.TOP:
                holder.itemView.setTranslationY(-holder.itemView.getHeight() / 3);
                break;
            case Gravity.END:
                holder.itemView.setTranslationX(holder.itemView.getWidth() / 3);
                break;
            default: // Gravity.BOTTOM
                holder.itemView.setTranslationY(holder.itemView.getHeight() / 3);
        }
        pendingAdds.add(holder);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        if (useDefaultAnimator) {
            super.runPendingAnimations();
            return;
        }
        if (!pendingAdds.isEmpty() && !useDefaultAnimator) {
            for (int i = pendingAdds.size() - 1; i >= 0; i--) {
                final RecyclerView.ViewHolder holder = pendingAdds.get(i);
                new Handler().postDelayed(() -> holder.itemView.animate()
                                .alpha(1f)
                                .translationX(0f)
                                .translationY(0f)
                                .setDuration(getAddDuration())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        dispatchAddStarting(holder);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        animation.getListeners().remove(this);
                                        dispatchAddFinished(holder);
                                        dispatchFinishedWhenDone();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        clearAnimatedValues(holder.itemView);
                                    }
                                })
                                .setInterpolator(new LinearOutSlowInInterpolator()),
                        holder.getAdapterPosition() * 30);
                pendingAdds.remove(i);
            }
            useDefaultAnimator = true;
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder holder) {
        holder.itemView.animate().cancel();
        if (pendingAdds.remove(holder)) {
            dispatchAddFinished(holder);
            clearAnimatedValues(holder.itemView);
        }
        super.endAnimation(holder);
    }

    @Override
    public void endAnimations() {
        for (int i = pendingAdds.size() - 1; i >= 0; i--) {
            final RecyclerView.ViewHolder holder = pendingAdds.get(i);
            clearAnimatedValues(holder.itemView);
            dispatchAddFinished(holder);
            pendingAdds.remove(i);
        }
        super.endAnimations();
    }

    @Override
    public boolean isRunning() {
        return !pendingAdds.isEmpty() || super.isRunning();
    }

    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    private void clearAnimatedValues(final View view) {
        view.setAlpha(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.animate().setStartDelay(0);
    }

}