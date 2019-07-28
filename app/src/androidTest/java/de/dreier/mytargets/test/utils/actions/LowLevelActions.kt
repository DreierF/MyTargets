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

import android.graphics.Matrix
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.platform.app.InstrumentationRegistry
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable
import org.hamcrest.Matcher

object LowLevelActions {
    private var sMotionEventDownHeldView: MotionEvent? = null

    fun pressAndHold(coordinates: FloatArray): PressAndHoldAction {
        return PressAndHoldAction(coordinates)
    }

    fun release(coordinates: FloatArray): ReleaseAction {
        return ReleaseAction(coordinates)
    }

    fun tearDown() {
        sMotionEventDownHeldView = null
    }

    fun getTargetCoordinates(v: View, coordinates: FloatArray): FloatArray {
        val screenPos = IntArray(2)
        v.getLocationOnScreen(screenPos)
        val contentWidth = v.width.toFloat()
        val contentHeight = v.height.toFloat()

        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources
            .displayMetrics.density

        val targetRectExt = RectF(0f, 80 * density, contentWidth, contentHeight)
        targetRectExt.inset(10 * density, 10 * density)
        if (targetRectExt.height() > targetRectExt.width()) {
            targetRectExt.top = targetRectExt.bottom - targetRectExt.width()
        }

        targetRectExt.inset(30 * density, 30 * density)
        val fullExtendedMatrix = Matrix()
        fullExtendedMatrix
            .setRectToRect(TargetDrawable.SRC_RECT, targetRectExt, Matrix.ScaleToFit.CENTER)

        val shotPos = FloatArray(2)
        fullExtendedMatrix.mapPoints(shotPos, coordinates)
        shotPos[0] += screenPos[0].toFloat()
        shotPos[1] += screenPos[1].toFloat()
        return shotPos
    }

    fun getAbsoluteCoordinates(v: View, coordinates: FloatArray): FloatArray {
        val screenPos = IntArray(2)
        v.getLocationOnScreen(screenPos)
        coordinates[0] += screenPos[0].toFloat()
        coordinates[1] += screenPos[1].toFloat()
        return coordinates
    }

    class PressAndHoldAction internal constructor(internal val coordinates: FloatArray) :
        ViewAction {

        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun getDescription(): String {
            return "Press and hold action"
        }

        override fun perform(uiController: UiController, view: View) {
            if (sMotionEventDownHeldView != null) {
                throw AssertionError("Only one view can be held at a time")
            }

            val precision = Press.FINGER.describePrecision()
            sMotionEventDownHeldView = MotionEvents
                .sendDown(
                    uiController, getTargetCoordinates(view, coordinates),
                    precision
                ).down
            // TODO: save view information and make sure release() is on same view

        }
    }

    class ReleaseAction(private val coordinates: FloatArray) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun getDescription(): String {
            return "Release action"
        }

        override fun perform(uiController: UiController, view: View) {
            if (sMotionEventDownHeldView == null) {
                throw AssertionError(
                    "Before calling release(), you must call pressAndHold() on a view"
                )
            }

            MotionEvents.sendUp(
                uiController, sMotionEventDownHeldView!!,
                getTargetCoordinates(view, coordinates)
            )
        }
    }
}
