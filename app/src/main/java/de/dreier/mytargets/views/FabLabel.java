package de.dreier.mytargets.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import de.dreier.mytargets.R;

public class FabLabel extends TextView {
    private int mRawWidth;
    private int mRawHeight;
    private Animation mShowAnimation;
    private Animation mHideAnimation;

    public FabLabel(Context context) {
        super(context);
        init();
    }

    public FabLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FabLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_label_show);
        mHideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_label_hide);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(calculateMeasuredWidth(), calculateMeasuredHeight());
    }

    private int calculateMeasuredWidth() {
        if (mRawWidth == 0) {
            mRawWidth = getMeasuredWidth();
        }
        return getMeasuredWidth();
    }

    private int calculateMeasuredHeight() {
        if (mRawHeight == 0) {
            mRawHeight = getMeasuredHeight();
        }
        return getMeasuredHeight();
    }

    private void playShowAnimation() {
        if (mShowAnimation != null) {
            mHideAnimation.cancel();
            startAnimation(mShowAnimation);
        }
    }

    private void playHideAnimation() {
        if (mHideAnimation != null) {
            mShowAnimation.cancel();
            startAnimation(mHideAnimation);
        }
    }

    public void show(boolean animate) {
        if (animate) {
            playShowAnimation();
        }
        setVisibility(VISIBLE);
        setClickable(true);
    }

    public void hide(boolean animate) {
        if (animate) {
            playHideAnimation();
        }
        setVisibility(INVISIBLE);
        setClickable(false);
    }
}