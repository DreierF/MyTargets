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

package de.dreier.mytargets.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

/**
 * Created by Matteo on 08/08/2015.
 *
 * Floating Action Menu Behavior for Clans.FloatingActionButton
 * https://github.com/Clans/FloatingActionButton/
 *
 * Use this behavior as your app:layout_behavior attribute in your Floating Action Menu to use the
 * FabMenu in a Coordinator Layout.
 *
 * Remember to use the correct namespace for the fab:
 * xmlns:fab="http://schemas.android.com/apk/res-auto"
 */
public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {
    private float mTranslationY;

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        if (child != null && dependency instanceof Snackbar.SnackbarLayout) {
            this.updateTranslation(parent, child, dependency);
        }

        return false;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        float translationY = Math.min(0, parent.getBottom() - child.getBottom());
        child.setTranslationY(translationY);
    }

    private void updateTranslation(CoordinatorLayout parent, View child, View dependency) {
        float translationY = this.getTranslationY(parent, child);
        if (translationY != this.mTranslationY) {
            ViewCompat.animate(child)
                    .cancel();
            if (Math.abs(translationY - this.mTranslationY) == (float) dependency.getHeight()) {
                ViewCompat.animate(child)
                        .translationY(translationY)
                        .setListener(null);
            } else {
                ViewCompat.setTranslationY(child, translationY);
            }

            this.mTranslationY = translationY;
        }
    }

    private float getTranslationY(CoordinatorLayout parent, View child) {
        float minOffset = 0.0F;
        List dependencies = parent.getDependencies(child);
        int i = 0;

        for (int z = dependencies.size(); i < z; ++i) {
            View view = (View) dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(child, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float) view.getHeight());
            }
        }

        return minOffset;
    }
}