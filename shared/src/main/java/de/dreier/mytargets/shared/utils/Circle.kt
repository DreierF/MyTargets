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
import android.graphics.Paint
import android.text.TextPaint
import de.dreier.mytargets.shared.models.Target

class Circle(private val density: Float) {
    private val circleColorPaint = Paint()
    private val textPaint = TextPaint()

    init {
        // Set up default Paint object
        circleColorPaint.isAntiAlias = true

        // Set up a default TextPaint object
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.textAlign = Paint.Align.CENTER
    }

    fun draw(can: Canvas, x: Float, y: Float, zone: Int, radius: Int, arrow: Int, number: String?, ambientMode: Boolean, target: Target) {
        val zoneBase = target.model.getZone(zone)
        val fillColor = if (ambientMode) Color.BLACK else zoneBase.fillColor
        val borderColor = if (ambientMode) Color.WHITE else Color.getStrokeColor(zoneBase.fillColor)
        val textColor = if (ambientMode) Color.WHITE else zoneBase.textColor
        val score = target.zoneToString(zone, arrow)
        drawScore(can, x, y, radius * density, score,
                if (ambientMode) null else number, fillColor, borderColor, textColor)
    }

    fun drawScore(canvas: Canvas, x: Float, y: Float, radius: Float, score: String, arrowNumber: String?, fillColor: Int, borderColor: Int, textColor: Int) {
        val fontSize = (1.2323f * radius + 0.7953f).toInt()

        // Draw the circles background
        circleColorPaint.strokeWidth = 2f
        circleColorPaint.style = Paint.Style.FILL_AND_STROKE
        circleColorPaint.color = fillColor
        canvas.drawCircle(x, y, radius, circleColorPaint)

        // Draw the circles border
        circleColorPaint.style = Paint.Style.STROKE
        circleColorPaint.color = borderColor
        canvas.drawCircle(x, y, radius, circleColorPaint)

        // Draw the text inside the circle
        textPaint.color = textColor
        textPaint.textSize = fontSize.toFloat()
        canvas.drawText(score, x, y + fontSize * 7 / 22.0f, textPaint)

        if (arrowNumber != null) {
            circleColorPaint.style = Paint.Style.FILL_AND_STROKE
            circleColorPaint.color = -0xcccccd
            canvas.drawCircle(x + radius * 0.8f, y + radius * 0.8f, radius * 0.5f, circleColorPaint)
            textPaint.textSize = fontSize * 0.5f
            textPaint.color = -0x1
            canvas.drawText(arrowNumber, x + radius * 0.8f, y + radius * 1.05f, textPaint)
        }
    }
}
