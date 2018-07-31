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

import android.graphics.Paint
import android.graphics.PointF

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper
import de.dreier.mytargets.shared.utils.Color

abstract class ZoneBase(
        val radius: Float,
        protected val midpoint: PointF,
        val fillColor: Int,
        val strokeColor: Int,
        strokeWidth: Int,
        protected val scoresAsOutsideIn: Boolean
) {
    val strokeWidth: Float = strokeWidth * 0.002f

    internal val paintFill: Paint by lazy {
        val paintFill = Paint()
        paintFill.isAntiAlias = true
        paintFill.color = fillColor
        paintFill
    }

    internal val paintStroke: Paint by lazy {
        val paintStroke = Paint()
        paintStroke.style = Paint.Style.STROKE
        paintStroke.isAntiAlias = true
        paintStroke.color = strokeColor
        paintStroke.strokeWidth = this.strokeWidth
        paintStroke
    }

    val textColor: Int
        get() = Color.getContrast(fillColor)

    abstract fun isInZone(ax: Float, ay: Float, arrowRadius: Float): Boolean

    abstract fun drawFill(canvas: CanvasWrapper)

    abstract fun drawStroke(canvas: CanvasWrapper)
}
