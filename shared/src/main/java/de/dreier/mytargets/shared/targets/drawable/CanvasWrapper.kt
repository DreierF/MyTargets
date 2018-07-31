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

package de.dreier.mytargets.shared.targets.drawable

import android.graphics.*
import android.text.TextPaint

class CanvasWrapper {

    private val tempPen = Paint()
    // two points 1 unit away from each other
    private val PTS = floatArrayOf(0f, 0f, 1f, 0f)
    private val tmpPts = FloatArray(4)
    private val tmpPt = FloatArray(2)
    private val tmpPath = Path()
    private var canvas: Canvas? = null
    private var matrix: Matrix = Matrix()
    private var scale: Float = 0f
    private val tempTextPaint = TextPaint()

    fun setCanvas(canvas: Canvas) {
        this.canvas = canvas
    }

    fun releaseCanvas() {
        canvas = null
    }

    fun setMatrix(matrix: Matrix) {
        this.matrix = matrix

        // get the scale for transforming the Paint
        this.matrix.mapPoints(tmpPts, PTS)
        scale = Math.sqrt(Math.pow((tmpPts[0] - tmpPts[2]).toDouble(), 2.0) + Math.pow((tmpPts[1] - tmpPts[3]).toDouble(), 2.0)).toFloat()
    }

    fun drawPath(path: Path, pen: Paint) {
        // transform the path
        tmpPath.set(path)
        tmpPath.transform(matrix)

        // copy the existing Paint
        scalePaint(pen)

        // draw the path
        canvas!!.drawPath(tmpPath, tempPen)
    }

    fun drawCircle(x: Float, y: Float, radius: Float, paintFill: Paint) {
        // copy the existing Paint
        scalePaint(paintFill)
        tmpPt[0] = x
        tmpPt[1] = y
        this.matrix.mapPoints(tmpPt)
        canvas!!.drawCircle(tmpPt[0], tmpPt[1], radius * scale, tempPen)
    }

    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paintStroke: Paint) {
        // copy the existing Paint
        scalePaint(paintStroke)
        tmpPts[0] = startX
        tmpPts[1] = startY
        tmpPts[2] = endX
        tmpPts[3] = endY
        this.matrix.mapPoints(tmpPts)
        canvas!!.drawLine(tmpPts[0], tmpPts[1], tmpPts[2], tmpPts[3], tempPen)
    }

    fun drawRect(rect: RectF, paint: Paint) {
        scalePaint(paint)
        tmpPts[0] = rect.left
        tmpPts[1] = rect.top
        tmpPts[2] = rect.right
        tmpPts[3] = rect.bottom
        this.matrix.mapPoints(tmpPts)
        canvas!!.drawRect(tmpPts[0], tmpPts[1], tmpPts[2], tmpPts[3], tempPen)
    }

    private fun scalePaint(paint: Paint) {
        tempPen.set(paint)
        tempPen.strokeMiter = paint.strokeMiter * scale
        tempPen.strokeWidth = paint.strokeWidth * scale
    }

    fun drawText(text: String, rect: RectF, paintText: TextPaint) {
        tmpPts[0] = rect.left
        tmpPts[1] = rect.top
        tmpPts[2] = rect.right
        tmpPts[3] = rect.bottom
        this.matrix.mapPoints(tmpPts)
        rect.left = tmpPts[0]
        rect.top = tmpPts[1]
        rect.right = tmpPts[2]
        rect.bottom = tmpPts[3]

        tempTextPaint.set(paintText)
        tempTextPaint.textAlign = Paint.Align.CENTER
        tempTextPaint.textSize = rect.height() * 0.8f
        canvas!!.drawText(text, rect.centerX(), rect.bottom - rect.height() * 0.2f, tempTextPaint)
    }
}
