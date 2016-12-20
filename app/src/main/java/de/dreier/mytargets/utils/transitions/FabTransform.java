/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.utils.transitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * A transition between a FAB & another surface using a circular reveal moving along an arc.
 * <p>
 * See: https://www.google.com/design/spec/motion/transforming-material.html#transforming-material-radial-transformation
 */
@TargetApi(LOLLIPOP)
public class FabTransform extends Transition {

    private static final String EXTRA_FAB_COLOR_RES = "EXTRA_FAB_COLOR";
    private static final String EXTRA_FAB_ICON_RES_ID = "EXTRA_FAB_ICON_RES_ID";
    private static final long DEFAULT_DURATION = 240L;
    private static final String PROP_BOUNDS = "plaid:fabTransform:bounds";
    private static final String[] TRANSITION_PROPERTIES = {
            PROP_BOUNDS
    };

    @ColorInt
    private final int color;
    private final int icon;

    private FabTransform(@ColorInt int fabColor, @DrawableRes int fabIconResId) {
        color = fabColor;
        icon = fabIconResId;
        setPathMotion(new GravityArcMotion());
        setDuration(DEFAULT_DURATION);
    }

    public FabTransform(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.FabTransform);
            if (!a.hasValue(R.styleable.FabTransform_fabColor)
                    || !a.hasValue(R.styleable.FabTransform_fabIcon)) {
                throw new IllegalArgumentException("Must provide both color & icon.");
            }
            color = a.getColor(R.styleable.FabTransform_fabColor, Color.TRANSPARENT);
            icon = a.getResourceId(R.styleable.FabTransform_fabIcon, 0);
            setPathMotion(new GravityArcMotion());
            if (getDuration() < 0) {
                setDuration(DEFAULT_DURATION);
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Configure {@code intent} with the extras needed to initialize this transition.
     */
    public static void addExtras(@NonNull Intent intent, @ColorRes int fabColor,
                                 @DrawableRes int fabIconResId) {
        intent.putExtra(EXTRA_FAB_COLOR_RES, fabColor);
        intent.putExtra(EXTRA_FAB_ICON_RES_ID, fabIconResId);
    }

    /**
     * Create a {@link FabTransform} from the supplied {@code activity} extras and set as its
     * shared element enter/return transition.
     */
    static boolean setup(@NonNull Activity activity, @Nullable View target) {
        final Intent intent = activity.getIntent();
        if (!intent.hasExtra(EXTRA_FAB_COLOR_RES) || !intent.hasExtra(EXTRA_FAB_ICON_RES_ID)) {
            return false;
        }

        final int colorRes = intent.getIntExtra(EXTRA_FAB_COLOR_RES, R.color.colorAccent);
        final int icon = intent.getIntExtra(EXTRA_FAB_ICON_RES_ID, -1);
        final int color = ContextCompat.getColor(activity, colorRes);
        final FabTransform sharedEnter = new FabTransform(color, icon);
        if (target != null) {
            sharedEnter.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        return true;
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(@NonNull final ViewGroup sceneRoot,
                                   final TransitionValues startValues,
                                   final TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
        final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);

        final boolean fromFab = endBounds.width() > startBounds.width();
        final View view = endValues.view;
        final Rect dialogBounds = fromFab ? endBounds : startBounds;
        final Rect fabBounds = fromFab ? startBounds : endBounds;
        final Interpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();
        final long duration = getDuration();
        final long halfDuration = duration / 2;
        final long twoThirdsDuration = duration * 2 / 3;

        if (!fromFab) {
            // Force measure / layout the dialog back to it's original bounds
            view.measure(
                    makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                    makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY));
            view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
        }

        final int translationX = startBounds.centerX() - endBounds.centerX();
        final int translationY = startBounds.centerY() - endBounds.centerY();
        if (fromFab) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }

        // Add a color overlay to fake appearance of the FAB
        final ColorDrawable fabColor = new ColorDrawable(color);
        fabColor.setBounds(0, 0, dialogBounds.width(), dialogBounds.height());
        if (!fromFab) {
            fabColor.setAlpha(0);
        }
        view.getOverlay().add(fabColor);

        // Add an icon overlay again to fake the appearance of the FAB
        final Drawable fabIcon =
                ContextCompat.getDrawable(sceneRoot.getContext(), icon).mutate();
        final int iconLeft = (dialogBounds.width() - fabIcon.getIntrinsicWidth()) / 2;
        final int iconTop = (dialogBounds.height() - fabIcon.getIntrinsicHeight()) / 2;
        fabIcon.setBounds(iconLeft, iconTop,
                iconLeft + fabIcon.getIntrinsicWidth(),
                iconTop + fabIcon.getIntrinsicHeight());
        if (!fromFab) {
            fabIcon.setAlpha(0);
        }
        view.getOverlay().add(fabIcon);

        // Circular clip from/to the FAB size
        final Animator circularReveal;
        if (fromFab) {
            circularReveal = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2,
                    view.getHeight() / 2,
                    startBounds.width() / 2,
                    (float) Math.hypot(endBounds.width() / 2, endBounds.height() / 2));
            circularReveal.setInterpolator(new FastOutLinearInInterpolator());
        } else {
            circularReveal = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2,
                    view.getHeight() / 2,
                    (float) Math.hypot(startBounds.width() / 2, startBounds.height() / 2),
                    endBounds.width() / 2);
            circularReveal.setInterpolator(new LinearOutSlowInInterpolator());

            // Persist the end clip i.e. stay at FAB size after the reveal has run
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            final int left = (view.getWidth() - fabBounds.width()) / 2;
                            final int top = (view.getHeight() - fabBounds.height()) / 2;
                            outline.setOval(
                                    left, top, left + fabBounds.width(), top + fabBounds.height());
                            view.setClipToOutline(true);
                        }
                    });
                }
            });
        }
        circularReveal.setDuration(duration);

        // Translate to end position along an arc
        final Animator translate = ObjectAnimator.ofFloat(
                view,
                View.TRANSLATION_X,
                View.TRANSLATION_Y,
                fromFab ? getPathMotion().getPath(translationX, translationY, 0, 0)
                        : getPathMotion().getPath(0, 0, -translationX, -translationY));
        translate.setDuration(duration);
        translate.setInterpolator(fastOutSlowInInterpolator);

        // Fade contents of non-FAB view in/out
        List<Animator> fadeContents = null;
        if (view instanceof ViewGroup) {
            final ViewGroup vg = ((ViewGroup) view);
            fadeContents = new ArrayList<>(vg.getChildCount());
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                final View child = vg.getChildAt(i);
                final Animator fade =
                        ObjectAnimator.ofFloat(child, View.ALPHA, fromFab ? 1f : 0f);
                if (fromFab) {
                    child.setAlpha(0f);
                }
                fade.setDuration(twoThirdsDuration);
                fade.setInterpolator(fastOutSlowInInterpolator);
                fadeContents.add(fade);
            }
        }

        // Fade in/out the fab color & icon overlays
        final Animator colorFade = ObjectAnimator.ofInt(fabColor, "alpha", fromFab ? 0 : 255);
        final Animator iconFade = ObjectAnimator.ofInt(fabIcon, "alpha", fromFab ? 0 : 255);
        if (!fromFab) {
            colorFade.setStartDelay(halfDuration);
            iconFade.setStartDelay(halfDuration);
        }
        colorFade.setDuration(halfDuration);
        iconFade.setDuration(halfDuration);
        colorFade.setInterpolator(fastOutSlowInInterpolator);
        iconFade.setInterpolator(fastOutSlowInInterpolator);

        // Work around issue with elevation shadows. At the end of the return transition the shared
        // element's shadow is drawn twice (by each activity) which is jarring. This workaround
        // still causes the shadow to snap, but it's better than seeing it double drawn.
        Animator elevation = null;
        if (!fromFab) {
            elevation = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, -view.getElevation());
            elevation.setDuration(duration);
            elevation.setInterpolator(fastOutSlowInInterpolator);
        }

        // Run all animations together
        final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(circularReveal, translate, colorFade, iconFade);
        transition.playTogether(fadeContents);
        if (elevation != null) {
            transition.play(elevation);
        }
        if (fromFab) {
            transition.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Clean up
                    view.getOverlay().clear();
                }
            });
        }
        return new NoPauseAnimator(transition);
    }

    private void captureValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }

        transitionValues.values.put(PROP_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                view.getRight(), view.getBottom()));
    }
}