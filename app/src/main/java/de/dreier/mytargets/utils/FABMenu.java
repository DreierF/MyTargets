package de.dreier.mytargets.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FABMenu {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.fab1)
    FloatingActionButton fab1;
    @Bind(R.id.fab2)
    FloatingActionButton fab2;
    @Bind(R.id.fab1Label)
    TextView fab1Label;
    @Bind(R.id.fab2Label)
    TextView fab2Label;
    @Bind(R.id.overlayView)
    View overlayView;
    @Bind(R.id.new_layout)
    View mNewLayout;
    @Bind(R.id.new_text)
    TextView mNewText;
    private boolean isFabOpen = false;
    private final Animation fabOpen;
    private final Animation fabClose;
    private final Animation rotateForward;
    private final Animation rotateBackward;
    private final Animation fabShowAnimation;
    private final Animation fabHideAnimation;
    private Listener listener;

    public interface Listener {
        boolean isFABExpandable();

        void onFabClicked(int index);
    }

    public FABMenu(Context context, View root) {
        ButterKnife.bind(this, root);

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward);
        fabShowAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_label_show);
        fabHideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_label_hide);

        fabOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                applyFabState(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fabClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                applyFabState(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void reset() {
        if (!isFabOpen) {
            fab.setAnimation(null);
            fab1.setAnimation(null);
            fab2.setAnimation(null);
            fab1Label.setAnimation(null);
            fab2Label.setAnimation(null);
        }
        applyFabState(isFabOpen);
    }

    public void unbind() {
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.fab, R.id.fab1, R.id.fab2, R.id.fab1Label, R.id.fab2Label})
    public void onClick(View v) {
        if (listener == null)
            return;
        switch (v.getId()) {
            case R.id.fab:
                if (listener.isFABExpandable()) {
                    animateFAB();
                } else {
                    listener.onFabClicked(0);
                }
                break;
            case R.id.fab1Label:
            case R.id.fab1:
                listener.onFabClicked(1);
                isFabOpen = false;
                break;
            case R.id.fab2Label:
            case R.id.fab2:
                listener.onFabClicked(2);
                isFabOpen = false;
                break;
        }
    }

    @OnClick(R.id.overlayView)
    void dismissFabMenu() {
        animateFAB();
    }

    private void animateFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1Label.startAnimation(fabHideAnimation);
            fab2Label.startAnimation(fabHideAnimation);
            overlayView.setAlpha(1);
            overlayView.animate().alpha(0).start();
        } else {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1Label.startAnimation(fabShowAnimation);
            fab2Label.startAnimation(fabShowAnimation);
            overlayView.setAlpha(0);
            overlayView.setVisibility(VISIBLE);
            overlayView.animate().alpha(1).start();
        }
    }

    private void applyFabState(boolean state) {
        isFabOpen = state;
        final int visibility = isFabOpen ? VISIBLE : INVISIBLE;
        fab1Label.setVisibility(visibility);
        fab2Label.setVisibility(visibility);
        fab1.setClickable(isFabOpen);
        fab2.setClickable(isFabOpen);
        fab1Label.setClickable(isFabOpen);
        fab2Label.setClickable(isFabOpen);
        overlayView.setVisibility(visibility);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        isFabOpen = savedInstanceState.getBoolean("fab_open");
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("fab_open", isFabOpen);
    }

    public void notifyContentChanged() {
        if (isFabOpen && !listener.isFABExpandable()) {
            animateFAB();
        }
    }

    public void setFABHelperTitle(int stringRes) {
        if (stringRes == 0) {
            mNewLayout.setVisibility(View.GONE);
        } else {
            mNewLayout.setVisibility(VISIBLE);
            mNewText.setText(stringRes);
        }
    }

    public void setFABItem(int index, @DrawableRes int icon, @StringRes int text) {
        switch (index) {
            case 1:
                fab1.setImageResource(icon);
                fab1Label.setText(text);
                break;
            case 2:
                fab2.setImageResource(icon);
                fab2Label.setText(text);
                break;
        }
    }
}
