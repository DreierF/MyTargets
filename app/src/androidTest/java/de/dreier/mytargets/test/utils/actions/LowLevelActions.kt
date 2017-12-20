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

package de.dreier.mytargets.test.utils.actions;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Press;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;

import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

public class LowLevelActions {
    @Nullable
    private static MotionEvent sMotionEventDownHeldView = null;

    public static PressAndHoldAction pressAndHold(float[] coordinates) {
        return new PressAndHoldAction(coordinates);
    }

    public static ReleaseAction release(float[] coordinates) {
        return new ReleaseAction(coordinates);
    }

    public static void tearDown() {
        sMotionEventDownHeldView = null;
    }

    @NonNull
    public static float[] getTargetCoordinates(@NonNull View v, float[] coordinates) {
        final int[] screenPos = new int[2];
        v.getLocationOnScreen(screenPos);
        float contentWidth = v.getWidth();
        float contentHeight = v.getHeight();

        float density = InstrumentationRegistry.getTargetContext().getResources()
                .getDisplayMetrics().density;

        RectF targetRectExt = new RectF(0, 80 * density, contentWidth, contentHeight);
        targetRectExt.inset(10 * density, 10 * density);
        if (targetRectExt.height() > targetRectExt.width()) {
            targetRectExt.top = targetRectExt.bottom - targetRectExt.width();
        }

        targetRectExt.inset(30 * density, 30 * density);
        Matrix fullExtendedMatrix = new Matrix();
        fullExtendedMatrix
                .setRectToRect(TargetDrawable.Companion.getSRC_RECT(), targetRectExt, Matrix.ScaleToFit.CENTER);

        final float[] shotPos = new float[2];
        fullExtendedMatrix.mapPoints(shotPos, coordinates);
        shotPos[0] += screenPos[0];
        shotPos[1] += screenPos[1];
        return shotPos;
    }

    @NonNull
    public static float[] getAbsoluteCoordinates(@NonNull View v, float[] coordinates) {
        final int[] screenPos = new int[2];
        v.getLocationOnScreen(screenPos);
        coordinates[0] += screenPos[0];
        coordinates[1] += screenPos[1];
        return coordinates;
    }

    public static class PressAndHoldAction implements ViewAction {
        final float[] coordinates;

        PressAndHoldAction(float[] coordinates) {
            this.coordinates = coordinates;
        }

        @NonNull
        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);
        }

        @NonNull
        @Override
        public String getDescription() {
            return "Press and hold action";
        }

        @Override
        public void perform(@NonNull final UiController uiController, @NonNull final View view) {
            if (sMotionEventDownHeldView != null) {
                throw new AssertionError("Only one view can be held at a time");
            }

            float[] precision = Press.FINGER.describePrecision();
            sMotionEventDownHeldView = MotionEvents
                    .sendDown(uiController, getTargetCoordinates(view, coordinates),
                            precision).down;
            // TODO: save view information and make sure release() is on same view

        }
    }

    public static class ReleaseAction implements ViewAction {
        private final float[] coordinates;

        public ReleaseAction(float[] coordinates) {
            this.coordinates = coordinates;
        }

        @NonNull
        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);
        }

        @NonNull
        @Override
        public String getDescription() {
            return "Release action";
        }

        @Override
        public void perform(@NonNull final UiController uiController, @NonNull final View view) {
            if (sMotionEventDownHeldView == null) {
                throw new AssertionError(
                        "Before calling release(), you must call pressAndHold() on a view");
            }

            MotionEvents.sendUp(uiController, sMotionEventDownHeldView,
                    getTargetCoordinates(view, coordinates));
        }
    }
}
