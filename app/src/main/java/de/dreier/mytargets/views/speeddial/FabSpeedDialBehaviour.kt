/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.views.speeddial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.util.List;

@SuppressWarnings("unused")
public class FabSpeedDialBehaviour extends CoordinatorLayout.Behavior<FabSpeedDial> {

    private static final FastOutSlowInInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR =
            new FastOutSlowInInterpolator();

    private ViewPropertyAnimator fabTranslationYAnimator;
    private float fabTranslationY;

    public FabSpeedDialBehaviour() {

    }

    public FabSpeedDialBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FabSpeedDial child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, @NonNull FabSpeedDial fab, View dependency) {
        super.onDependentViewRemoved(parent, fab, dependency);

        // Make sure that any current animation is cancelled
        if (fabTranslationYAnimator != null) {
            fabTranslationYAnimator.cancel();
        }

        fab.setTranslationY(0);
        fabTranslationY = 0;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FabSpeedDial child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(parent, child, dependency);
        }
        return false;
    }

    private void updateFabTranslationForSnackbar(@NonNull CoordinatorLayout parent, @NonNull final FabSpeedDial fab, View snackbar) {
        if (fab.getVisibility() != View.VISIBLE) {
            return;
        }

        final float targetTransY = getFabTranslationYForSnackbar(parent, fab);
        if (fabTranslationY == targetTransY) {
            // We're already at (or currently animating to) the target value, return...
            return;
        }

        final float currentTransY = fab.getTranslationY();

        // Make sure that any current animation is cancelled
        if (fabTranslationYAnimator != null) {
            fabTranslationYAnimator.cancel();
        }

        if (Math.abs(currentTransY - targetTransY) > (fab.getHeight() * 0.667f)) {
            fabTranslationYAnimator = fab.animate()
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .translationY(targetTransY);
            fabTranslationYAnimator.start();
        } else {
            fab.setTranslationY(targetTransY);
        }

        fabTranslationY = targetTransY;
    }

    private float getFabTranslationYForSnackbar(@NonNull CoordinatorLayout parent, @NonNull FabSpeedDial fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0; i < dependencies.size(); i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        view.getTranslationY() - view.getHeight());
            }
        }
        return minOffset;
    }
}
