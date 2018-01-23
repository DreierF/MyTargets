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

package de.dreier.mytargets.features.statistics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import de.dreier.mytargets.shared.utils.Color

/**
 * Copy of
 * https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartLib/src/main/java/com/github/mikephil/charting/renderer/PieChartRenderer.java
 *
 * Copied to implement https://stackoverflow.com/questions/38758885/border-around-pie-chart-in-mpchart#38775142
 */
class CustomPieChartRenderer(chart: PieChart, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : PieChartRenderer(chart, animator, viewPortHandler) {

    /**
     * paint object for the stroke around the slices
     */
    private val mStrokeRenderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mPathBuffer = Path()
    private val mInnerRectBuffer = RectF()

    init {
        mStrokeRenderPaint.style = Style.STROKE
        mStrokeRenderPaint.strokeWidth = Utils.convertDpToPixel(1f)
    }

    override fun drawDataSet(c: Canvas?, dataSet: IPieDataSet) {

        var angle = 0f
        val rotationAngle = mChart.rotationAngle

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val circleBox = mChart.circleBox

        val entryCount = dataSet.entryCount
        val drawAngles = mChart.drawAngles
        val center = mChart.centerCircleBox
        val radius = mChart.radius
        val drawInnerArc = mChart.isDrawHoleEnabled && !mChart.isDrawSlicesUnderHoleEnabled
        val userInnerRadius = if (drawInnerArc)
            radius * (mChart.holeRadius / 100f)
        else
            0f

        val visibleAngleCount = (0 until entryCount).count { // draw only if the value is greater than zero
            Math.abs(dataSet.getEntryForIndex(it).y) > Utils.FLOAT_EPSILON
        }

        val sliceSpace = if (visibleAngleCount <= 1) 0f else getSliceSpace(dataSet)

        for (j in 0 until entryCount) {

            val sliceAngle = drawAngles[j]
            var innerRadius = userInnerRadius

            val e = dataSet.getEntryForIndex(j)

            // draw only if the value is greater than zero
            if (Math.abs(e.y) > Utils.FLOAT_EPSILON) {

                if (!mChart.needsHighlight(j)) {

                    val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f

                    mRenderPaint.color = dataSet.getColor(j)

                    val sliceSpaceAngleOuter = if (visibleAngleCount == 1)
                        0f
                    else
                        sliceSpace / (Utils.FDEG2RAD * radius)
                    val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
                    var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
                    if (sweepAngleOuter < 0f) {
                        sweepAngleOuter = 0f
                    }

                    mPathBuffer.reset()

                    val arcStartPointX = center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()
                    val arcStartPointY = center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()

                    if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                        // Android is doing "mod 360"
                        mPathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW)
                    } else {

                        mPathBuffer.moveTo(arcStartPointX, arcStartPointY)

                        mPathBuffer.arcTo(
                                circleBox,
                                startAngleOuter,
                                sweepAngleOuter
                        )
                    }

                    // API < 21 does not receive floats in addArc, but a RectF
                    mInnerRectBuffer.set(
                            center.x - innerRadius,
                            center.y - innerRadius,
                            center.x + innerRadius,
                            center.y + innerRadius)

                    if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {

                        if (accountForSliceSpacing) {
                            var minSpacedRadius = calculateMinimumRadiusForSpacedSlice(
                                    center, radius,
                                    sliceAngle * phaseY,
                                    arcStartPointX, arcStartPointY,
                                    startAngleOuter,
                                    sweepAngleOuter)

                            if (minSpacedRadius < 0f) {
                                minSpacedRadius = -minSpacedRadius
                            }

                            innerRadius = Math.max(innerRadius, minSpacedRadius)
                        }

                        val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f)
                            0f
                        else
                            sliceSpace / (Utils.FDEG2RAD * innerRadius)
                        val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
                        var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
                        if (sweepAngleInner < 0f) {
                            sweepAngleInner = 0f
                        }
                        val endAngleInner = startAngleInner + sweepAngleInner

                        if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                            // Android is doing "mod 360"
                            mPathBuffer
                                    .addCircle(center.x, center.y, innerRadius, Path.Direction.CCW)
                        } else {

                            mPathBuffer.lineTo(
                                    center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
                                    center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat())

                            mPathBuffer.arcTo(
                                    mInnerRectBuffer,
                                    endAngleInner,
                                    -sweepAngleInner
                            )
                        }
                    } else {

                        if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                            if (accountForSliceSpacing) {

                                val angleMiddle = startAngleOuter + sweepAngleOuter / 2f

                                val sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(
                                        center,
                                        radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX,
                                        arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter)

                                val arcEndPointX = center.x + sliceSpaceOffset * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
                                val arcEndPointY = center.y + sliceSpaceOffset * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()

                                mPathBuffer.lineTo(
                                        arcEndPointX,
                                        arcEndPointY)

                            } else {
                                mPathBuffer.lineTo(
                                        center.x,
                                        center.y)
                            }
                        }

                    }

                    mPathBuffer.close()

                    mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint)

                    // MODIFIED CODE START

                    mStrokeRenderPaint.color = Color.getStrokeColor(dataSet.getColor(j))
                    mBitmapCanvas.drawPath(mPathBuffer, mStrokeRenderPaint)

                    // MODIFIED CODE END
                }
            }

            angle += sliceAngle * phaseX
        }

        MPPointF.recycleInstance(center)
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<Highlight>) {

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        var angle: Float
        val rotationAngle = mChart.rotationAngle

        val drawAngles = mChart.drawAngles
        val absoluteAngles = mChart.absoluteAngles
        val center = mChart.centerCircleBox
        val radius = mChart.radius
        val drawInnerArc = mChart.isDrawHoleEnabled && !mChart.isDrawSlicesUnderHoleEnabled
        val userInnerRadius = if (drawInnerArc)
            radius * (mChart.holeRadius / 100f)
        else
            0f

        val highlightedCircleBox = mDrawHighlightedRectF
        highlightedCircleBox.set(0f, 0f, 0f, 0f)

        for (i in indices.indices) {

            // get the index to highlight
            val index = indices[i].x.toInt()

            if (index >= drawAngles.size) {
                continue
            }

            val set = mChart.data
                    .getDataSetByIndex(indices[i]
                            .dataSetIndex)

            if (set == null || !set.isHighlightEnabled) {
                continue
            }

            val entryCount = set.entryCount
            val visibleAngleCount = (0 until entryCount).count {
                // draw only if the value is greater than zero
                Math.abs(set.getEntryForIndex(it).y) > Utils.FLOAT_EPSILON
            }

            angle = if (index == 0) {
                0f
            } else {
                absoluteAngles[index - 1] * phaseX
            }

            val sliceSpace = if (visibleAngleCount <= 1) 0f else set.sliceSpace

            val sliceAngle = drawAngles[index]
            var innerRadius = userInnerRadius

            val shift = set.selectionShift
            val highlightedRadius = radius + shift
            highlightedCircleBox.set(mChart.circleBox)
            highlightedCircleBox.inset(-shift, -shift)

            val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f

            mRenderPaint.color = set.getColor(index)

            val sliceSpaceAngleOuter = if (visibleAngleCount == 1)
                0f
            else
                sliceSpace / (Utils.FDEG2RAD * radius)

            val sliceSpaceAngleShifted = if (visibleAngleCount == 1)
                0f
            else
                sliceSpace / (Utils.FDEG2RAD * highlightedRadius)

            val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
            var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f
            }

            val startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2f) * phaseY
            var sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY
            if (sweepAngleShifted < 0f) {
                sweepAngleShifted = 0f
            }

            mPathBuffer.reset()

            if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW)
            } else {

                mPathBuffer.moveTo(
                        center.x + highlightedRadius * Math.cos((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat(),
                        center.y + highlightedRadius * Math.sin((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat())

                mPathBuffer.arcTo(
                        highlightedCircleBox,
                        startAngleShifted,
                        sweepAngleShifted
                )
            }

            var sliceSpaceRadius = 0f
            if (accountForSliceSpacing) {
                sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(
                        center, radius,
                        sliceAngle * phaseY,
                        center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
                        center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
                        startAngleOuter,
                        sweepAngleOuter)
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius)

            if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {

                if (accountForSliceSpacing) {
                    var minSpacedRadius = sliceSpaceRadius

                    if (minSpacedRadius < 0f) {
                        minSpacedRadius = -minSpacedRadius
                    }

                    innerRadius = Math.max(innerRadius, minSpacedRadius)
                }

                val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f)
                    0f
                else
                    sliceSpace / (Utils.FDEG2RAD * innerRadius)
                val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
                var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
                if (sweepAngleInner < 0f) {
                    sweepAngleInner = 0f
                }
                val endAngleInner = startAngleInner + sweepAngleInner

                if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW)
                } else {

                    mPathBuffer.lineTo(
                            center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
                            center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat())

                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    )
                }
            } else {

                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {

                    if (accountForSliceSpacing) {
                        val angleMiddle = startAngleOuter + sweepAngleOuter / 2f

                        val arcEndPointX = center.x + sliceSpaceRadius * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
                        val arcEndPointY = center.y + sliceSpaceRadius * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY)

                    } else {

                        mPathBuffer.lineTo(
                                center.x,
                                center.y)
                    }

                }

            }

            mPathBuffer.close()

            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint)

            // MODIFIED START

            mStrokeRenderPaint.color = Color.getStrokeColor(set.getColor(index))
            mBitmapCanvas.drawPath(mPathBuffer, mStrokeRenderPaint)

            // MODIFIED END
        }

        MPPointF.recycleInstance(center)
    }
}
