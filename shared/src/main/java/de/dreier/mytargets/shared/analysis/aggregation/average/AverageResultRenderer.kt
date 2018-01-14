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

package de.dreier.mytargets.shared.analysis.aggregation.average

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer
import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper

class AverageResultRenderer(private val average: Average) : IAggregationResultRenderer {
    private val stdDevPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stdDevPath = Path()
    private val symbolPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val symbolPath = Path()

    init {
        symbolPaint.setARGB(255, 37, 155, 36)
        symbolPaint.style = Paint.Style.STROKE
        symbolPaint.strokeWidth = 0.005f
        stdDevPaint.setARGB(150, 100, 0, 0)
        stdDevPaint.style = Paint.Style.FILL
    }

    override fun setColor(color: Int) {
        stdDevPaint.color = color
    }

    override fun onPrepareDraw() {
        val avg = average.average
        val nUniStdDev = average.nonUniformStdDev
        stdDevPath.rewind()
        stdDevPath.arcTo(RectF(
                average.weightedAverage.x - nUniStdDev.right,
                average.weightedAverage.y - nUniStdDev.bottom,
                average.weightedAverage.x + nUniStdDev.right,
                average.weightedAverage.y + nUniStdDev.bottom), 270.0f, 90.0f, true)
        stdDevPath.arcTo(RectF(
                average.weightedAverage.x - nUniStdDev.right,
                average.weightedAverage.y - nUniStdDev.top,
                average.weightedAverage.x + nUniStdDev.right,
                average.weightedAverage.y + nUniStdDev.top), 0.0f, 90.0f)
        stdDevPath.arcTo(RectF(
                average.weightedAverage.x - nUniStdDev.left,
                average.weightedAverage.y - nUniStdDev.top,
                average.weightedAverage.x + nUniStdDev.left,
                average.weightedAverage.y + nUniStdDev.top), 90.0f, 90.0f)
        stdDevPath.arcTo(RectF(
                average.weightedAverage.x - nUniStdDev.left,
                average.weightedAverage.y - nUniStdDev.bottom,
                average.weightedAverage.x + nUniStdDev.left,
                average.weightedAverage.y + nUniStdDev.bottom), 180.0f, 90.0f)

        val smallestNonUniStdDev = listOf(nUniStdDev.top, nUniStdDev.bottom, nUniStdDev.left, nUniStdDev.right).min()!!

        val tmp = smallestNonUniStdDev / 4.0f
        symbolPath.rewind()
        symbolPath.addCircle(avg.x, avg.y, smallestNonUniStdDev / 2.0f, Path.Direction.CW)
        symbolPath.moveTo(avg.x - nUniStdDev.left, avg.y - tmp)
        symbolPath.lineTo(avg.x - nUniStdDev.left, avg.y + tmp)
        symbolPath.moveTo(avg.x - nUniStdDev.left, avg.y)
        symbolPath.lineTo(avg.x + nUniStdDev.right, avg.y)
        symbolPath.moveTo(avg.x + nUniStdDev.right, avg.y - tmp)
        symbolPath.lineTo(avg.x + nUniStdDev.right, avg.y + tmp)
        symbolPath.moveTo(avg.x - tmp, avg.y + nUniStdDev.top)
        symbolPath.lineTo(avg.x + tmp, avg.y + nUniStdDev.top)
        symbolPath.moveTo(avg.x, avg.y + nUniStdDev.top)
        symbolPath.lineTo(avg.x, avg.y - nUniStdDev.bottom)
        symbolPath.moveTo(avg.x - tmp, avg.y - nUniStdDev.bottom)
        symbolPath.lineTo(avg.x + tmp, avg.y - nUniStdDev.bottom)
    }

    override fun onDraw(canvas: CanvasWrapper) {
        if (average.dataPointCount >= 3) {
            canvas.drawPath(stdDevPath, stdDevPaint)
            canvas.drawPath(symbolPath, symbolPaint)
        }
    }
}
