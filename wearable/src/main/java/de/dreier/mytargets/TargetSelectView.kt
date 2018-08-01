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

package de.dreier.mytargets

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.utils.Circle
import de.dreier.mytargets.shared.utils.EndRenderer
import de.dreier.mytargets.shared.views.TargetViewBase

class TargetSelectView : TargetViewBase {
    private var radius: Int = 0
    private var chinHeight: Int = 0
    private var circleRadius: Double = 0.toDouble()
    private var circle: Circle? = null
    private var chinBound: Float = 0.toFloat()
    private var ambientMode = false
    private val backspaceBackground = Paint()

    private val currentlySelectedZone: Int
        get() = if (currentShotIndex != EndRenderer.NO_SELECTION) {
            shots[currentShotIndex].scoringRing
        } else {
            Shot.NOTHING_SELECTED
        }

    override val selectedShotCircleRadius: Int
        get() = RADIUS_SELECTED

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        backspaceSymbol.setTint(-0x1)
        backspaceBackground.color = ContextCompat.getColor(context, R.color.md_wear_green_active_ui_element)
        backspaceBackground.isAntiAlias = true
    }

    internal fun setChinHeight(chinHeight: Int) {
        this.chinHeight = chinHeight
    }

    override fun initWithTarget(target: Target) {
        super.initWithTarget(target)
        circle = Circle(density)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw all possible points in a circular
        val curZone = currentlySelectedZone
        for (i in 0 until selectableZones.size) {
            val coordinate = getCircularCoordinates(i)
            if (i != curZone) {
                circle!!.draw(canvas, coordinate.x, coordinate.y, selectableZones[i].index,
                        17, currentShotIndex, null, ambientMode, target)
            }
        }

        if (!ambientMode) {
            canvas.drawCircle(radius.toFloat(), radius + 30 * density, 20 * density, backspaceBackground)
            drawBackspaceButton(canvas)
        }

        // Draw all points of this end in the center
        endRenderer.draw(canvas)
    }

    private fun getCircularCoordinates(zone: Int): PointF {
        val degree = Math.toRadians(zone * 360.0 / selectableZones.size.toDouble())
        val coordinate = PointF()
        coordinate.x = (radius + Math.cos(degree) * circleRadius).toFloat()
        coordinate.y = (radius + Math.sin(degree) * circleRadius).toFloat()
        if (coordinate.y > chinBound) {
            coordinate.y = chinBound
        }
        return coordinate
    }

    override fun getShotCoordinates(shot: Shot): PointF {
        return getCircularCoordinates(getSelectableZoneIndexFromShot(shot))
    }

    override fun updateLayoutBounds(width: Int, height: Int) {
        radius = (width / 2.0).toInt()
        chinBound = height - (chinHeight + 15) * density
        circleRadius = (radius - 25 * density).toDouble()
    }

    override fun getEndRect(): RectF {
        val endRect = RectF()
        endRect.left = radius - 45 * density
        endRect.right = radius + 45 * density
        endRect.top = (radius / 2).toFloat()
        endRect.bottom = radius.toFloat()
        return endRect
    }

    override fun getBackspaceButtonBounds(): Rect {
        val backspaceBounds = Rect()
        backspaceBounds.left = (radius - 20 * density).toInt()
        backspaceBounds.right = (radius + 20 * density).toInt()
        backspaceBounds.top = (radius + 10 * density).toInt()
        backspaceBounds.bottom = (radius + 50 * density).toInt()
        return backspaceBounds
    }

    override fun getSelectableZonePosition(i: Int): Rect {
        val coordinate = getCircularCoordinates(i)
        val rad = if (i == currentlySelectedZone) RADIUS_SELECTED else RADIUS_UNSELECTED
        val rect = Rect()
        rect.left = (coordinate.x - rad).toInt()
        rect.top = (coordinate.y - rad).toInt()
        rect.right = (coordinate.x + rad).toInt()
        rect.bottom = (coordinate.y + rad).toInt()
        return rect
    }

    override fun updateShotToPosition(shot: Shot, x: Float, y: Float): Boolean {
        val zones = selectableZones.size

        val xDiff = (x - radius).toDouble()
        val yDiff = (y - radius).toDouble()

        val perceptionRadius = radius - 50 * density
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perceptionRadius * perceptionRadius) {
            var degree = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - 180.0 / zones.toDouble()
            if (degree < 0) {
                degree += 360.0
            }
            val index = (zones * ((360.0 - degree) / 360.0)).toInt()
            shot.scoringRing = selectableZones[index].index
            return true
        }

        return false
    }

    override fun selectPreviousShots(motionEvent: MotionEvent, x: Float, y: Float): Boolean {
        return false
    }

    fun setAmbientMode(ambientMode: Boolean) {
        this.ambientMode = ambientMode
        endRenderer.setAmbientMode(ambientMode)
        invalidate()
    }

    companion object {
        const val RADIUS_SELECTED = 23
        const val RADIUS_UNSELECTED = 17
    }
}
