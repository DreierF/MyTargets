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

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;

public class TargetViewActions {
    public static ViewAction clickTarget(final float x, final float y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                view -> LowLevelActions.getTargetCoordinates(view, new float[]{x, y}),
                Press.PINPOINT, 0, 0);
    }

    public static ViewAction holdTapTarget(final float x, final float y) {
        return LowLevelActions.pressAndHold(new float[]{x, y});
    }

    public static ViewAction releaseTapTarget(final float x, final float y) {
        return LowLevelActions.release(new float[]{x, y});
    }
}
