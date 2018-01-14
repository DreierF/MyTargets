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

package de.dreier.mytargets.shared.targets.zone

import android.graphics.PointF

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper

class CircularZone @JvmOverloads constructor(
        radius: Float,
        midpointX: Float,
        midpointY: Float,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int,
        scoresAsOutsideIn: Boolean = true
) : ZoneBase(radius, PointF(midpointX, midpointY), fillColor, strokeColor, strokeWidth, scoresAsOutsideIn) {

    @JvmOverloads constructor(radius: Float, fillColor: Int, strokeColor: Int, strokeWidth: Int, scoresAsOutsideIn: Boolean = true) : this(radius, 0f, 0f, fillColor, strokeColor, strokeWidth, scoresAsOutsideIn) {}

    override fun isInZone(ax: Float, ay: Float, arrowRadius: Float): Boolean {
        val distance = (ax - midpoint.x) * (ax - midpoint.x) + (ay - midpoint.y) * (ay - midpoint.y)
        val adaptedRadius = radius + (if (scoresAsOutsideIn) 1f else -1f) * (arrowRadius + strokeWidth / 2.0f)
        return adaptedRadius * adaptedRadius > distance
    }

    override fun drawFill(canvas: CanvasWrapper) {
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintFill)
    }


    override fun drawStroke(canvas: CanvasWrapper) {
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintStroke)
    }

}
