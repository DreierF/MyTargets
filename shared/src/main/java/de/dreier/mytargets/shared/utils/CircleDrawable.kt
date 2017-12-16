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

package de.dreier.mytargets.shared.utils

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

class CircleDrawable(density: Float, private val score: String, private val arrowNumber: String, private val fillColor: Int, private val textColor: Int) : Drawable() {

    private val circle = Circle(density)
    private var x = 0f
    private var y = 0f
    private var radius = 0f

    override fun draw(canvas: Canvas) {
        circle.drawScore(canvas, x, y, radius, score, arrowNumber, fillColor, Color.getStrokeColor(fillColor), textColor)
    }

    override fun setAlpha(i: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun onBoundsChange(bounds: Rect) {
        x = bounds.exactCenterX()
        y = bounds.exactCenterY()
        radius = Math.min(bounds.width(), bounds.height()) * 0.45f
    }
}
