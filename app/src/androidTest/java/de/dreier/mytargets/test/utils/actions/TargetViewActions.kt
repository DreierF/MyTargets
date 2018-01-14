/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.test.utils.actions

import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import de.dreier.mytargets.features.training.input.TargetView
import org.junit.Assert.assertNotNull

object TargetViewActions {
    fun clickTarget(x: Float, y: Float): ViewAction {
        return GeneralClickAction(
                Tap.SINGLE,
                CoordinatesProvider { view -> LowLevelActions.getTargetCoordinates(view, floatArrayOf(x, y)) },
                Press.PINPOINT, 0, 0)
    }

    fun holdTapTarget(x: Float, y: Float): ViewAction {
        return LowLevelActions.pressAndHold(floatArrayOf(x, y))
    }

    fun releaseTapTarget(x: Float, y: Float): ViewAction {
        return LowLevelActions.release(floatArrayOf(x, y))
    }

    fun clickVirtualButton(description: String): ViewAction {
        return GeneralClickAction(
                Tap.SINGLE,
                CoordinatesProvider { view ->
                    val targetView = view as TargetView
                    val vv = targetView.virtualViews.firstOrNull { it.description == description }
                    assertNotNull("Did not find virtual view with description '$description'", vv)
                    LowLevelActions
                            .getAbsoluteCoordinates(view, floatArrayOf(vv!!.rect!!.exactCenterX(), vv
                                    .rect!!.exactCenterY()))
                },
                Press.PINPOINT, 0, 0)
    }
}
