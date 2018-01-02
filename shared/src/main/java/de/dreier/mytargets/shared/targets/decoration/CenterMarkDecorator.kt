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

package de.dreier.mytargets.shared.targets.decoration

import android.graphics.Paint

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper

open class CenterMarkDecorator(val color: Int, val size: Float, val stroke: Int, private val tilted: Boolean) : TargetDecorator {
    private val paintStroke: Paint by lazy {
        val paintStroke = Paint()
        paintStroke.style = Paint.Style.STROKE
        paintStroke.isAntiAlias = true
        paintStroke
    }

    override fun drawDecoration(canvas: CanvasWrapper) {
        paintStroke.color = color
        paintStroke.strokeWidth = stroke / 500f
        if (tilted) {
            canvas.drawLine(-size * 0.002f, -size * 0.002f,
                    size * 0.002f, size * 0.002f, paintStroke)
            canvas.drawLine(-size * 0.002f, size * 0.002f,
                    size * 0.002f, -size * 0.002f, paintStroke)
        } else {
            canvas.drawLine(-size * 0.002f, 0f, size * 0.002f, 0f, paintStroke)
            canvas.drawLine(0f, -size * 0.002f, 0f, size * 0.002f, paintStroke)
        }
    }
}
