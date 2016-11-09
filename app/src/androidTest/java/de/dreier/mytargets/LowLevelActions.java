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

package de.dreier.mytargets;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Press;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

public class LowLevelActions {
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
    static float[] getTargetCoordinates(View v, float[] coordinates) {
        final int[] screenPos = new int[2];
        v.getLocationOnScreen(screenPos);
        float contentWidth = v.getWidth();
        float contentHeight = v.getHeight();

        float density = InstrumentationRegistry.getTargetContext().getResources()
                .getDisplayMetrics().density;

        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = ((int) contentWidth - 20 * density) * 0.5f;
        int orgRadius = (int) (Math.min(radW, radH));
        int orgMidX = (int) contentWidth / 2;
        float orgMidY = contentHeight - orgRadius - 10 * density;

        float x = coordinates[0] * (orgRadius - 30 * density) + orgMidX;
        float y = coordinates[1] * (orgRadius - 30 * density) + orgMidY;

        final float screenX = screenPos[0] + x;
        final float screenY = screenPos[1] + y;

        return new float[]{screenX, screenY};
    }

    public static class PressAndHoldAction implements ViewAction {
        final float[] coordinates;

        PressAndHoldAction(float[] coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);
        }

        @Override
        public String getDescription() {
            return "Press and hold action";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
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

        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);
        }

        @Override
        public String getDescription() {
            return "Release action";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            if (sMotionEventDownHeldView == null) {
                throw new AssertionError(
                        "Before calling release(), you must call pressAndHold() on a view");
            }

            MotionEvents.sendUp(uiController, sMotionEventDownHeldView,
                    getTargetCoordinates(view, coordinates));
        }
    }
}