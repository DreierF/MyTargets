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
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class FabSpeedDialBehaviour extends CoordinatorLayout.Behavior<FabSpeedDial> {


    private static final FastOutSlowInInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR =
            new FastOutSlowInInterpolator();

    private ViewPropertyAnimatorCompat mFabTranslationYAnimator;
    private float mFabTranslationY;
    private Rect mTmpRect;

    public FabSpeedDialBehaviour() {

    }

    public FabSpeedDialBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FabSpeedDial child, View dependency) {
        // We're dependent on all SnackbarLayouts
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FabSpeedDial fab, View dependency) {
        super.onDependentViewRemoved(parent, fab, dependency);

        // Make sure that any current animation is cancelled
        if (mFabTranslationYAnimator != null) {
            mFabTranslationYAnimator.cancel();
        }

        fab.setTranslationY(0);
        mFabTranslationY = 0;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FabSpeedDial child,
                                          View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(parent, child, dependency);
        } else if (dependency instanceof AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the FAB is anchored to the AppBarLayout
            updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }

    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
                                                 final FabSpeedDial fab, View snackbar) {
        if (fab.getVisibility() != View.VISIBLE) {
            return;
        }

        final float targetTransY = getFabTranslationYForSnackbar(parent, fab);
        if (mFabTranslationY == targetTransY) {
            // We're already at (or currently animating to) the target value, return...
            return;
        }

        final float currentTransY = fab.getTranslationY();

        // Make sure that any current animation is cancelled
        if (mFabTranslationYAnimator != null) {
            mFabTranslationYAnimator.cancel();
        }

        if (Math.abs(currentTransY - targetTransY) > (fab.getHeight() * 0.667f)) {
            mFabTranslationYAnimator = ViewCompat.animate(fab)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .translationY(targetTransY);
            mFabTranslationYAnimator.start();
        } else {
            fab.setTranslationY(targetTransY);
        }

        mFabTranslationY = targetTransY;
    }

    private float getFabTranslationYForSnackbar(CoordinatorLayout parent,
                                                FabSpeedDial fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        view.getTranslationY() - view.getHeight());
            }
        }

        return minOffset;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FabSpeedDial child, int layoutDirection) {
        // First, lets make sure that the visibility of the FAB is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout
                    && updateFabVisibility(parent, (AppBarLayout) dependency, child)) {
                break;
            }
        }
        // Now let the CoordinatorLayout lay out the FAB
        parent.onLayoutChild(child, layoutDirection);
        return true;
    }

    private boolean updateFabVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout,
                                        FabSpeedDial child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.getAnchorId() != appBarLayout.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the FAB
            return false;
        }

        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }

        // First, let's get the visible rect of the dependency
        final Rect rect = mTmpRect;
        ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);

        /*
         * TODO: Remove reflection and replace with
         * AppBarLayout#getMinimumHeightForVisibleOverlappingContent() once made publuc
         */
        int minimumHeightForVisibleOverlappingContent =
                getMinimumHeightForVisibleOverlappingContent(appBarLayout);
        if (minimumHeightForVisibleOverlappingContent == -1) { // the api has changed, return
            return true;
        }
        if (rect.bottom <= minimumHeightForVisibleOverlappingContent) {
            // If the anchor's bottom is below the seam, we'll animate our FAB out
            //child.hide();
        } else {
            // Else, we'll animate our FAB back in
            //child.show();
        }
        return true;
    }

    private int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        try {
            Method method = appBarLayout.getClass()
                    .getDeclaredMethod("getMinimumHeightForVisibleOverlappingContent");
            method.setAccessible(true);
            Object value = method.invoke(appBarLayout, null);
            return (int) value;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
