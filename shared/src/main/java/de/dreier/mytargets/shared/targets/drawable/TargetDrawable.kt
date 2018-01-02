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

package de.dreier.mytargets.shared.targets.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.TargetModelBase
import de.dreier.mytargets.shared.targets.zone.ZoneBase
import java.util.*

open class TargetDrawable(val target: Target) : Drawable() {
    val model: TargetModelBase = target.model
    private val zonesToDraw: List<ZoneBase>
    private val targetFaceMatrices: ArrayList<Matrix>
    private val drawMatrices: ArrayList<Matrix>
    private var matrix = Matrix()
    private var zoom = 1f
    private var px: Float = 0f
    private var py: Float = 0f
    var spotMatrix = Matrix()
    private var xOffset: Float = 0f
    private var yOffset: Float = 0f
    private val canvasWrapper = CanvasWrapper()

    private val zones: Int
        get() = model.zoneCount

    init {
        this.zonesToDraw = (zones - 1 downTo 0)
                .filter { model.shouldDrawZone(it, target.scoringStyleIndex) }
                .map { model.getZone(it) }
        targetFaceMatrices = ArrayList()
        drawMatrices = ArrayList()
        for (faceIndex in 0 until model.faceCount) {
            targetFaceMatrices.add(calculateTargetFaceMatrix(faceIndex))
            drawMatrices.add(Matrix())
        }
    }

    fun setMatrix(matrix: Matrix) {
        this.matrix = matrix
    }

    override fun getIntrinsicHeight() = 2000

    override fun getIntrinsicWidth() = 2000

    override fun onBoundsChange(bounds: Rect) {
        val srcRectWithStroke = RectF(SRC_RECT)
        val outerZone = model.getZone(model.zoneCount - 1)
        val inset = -outerZone.strokeWidth * 0.5f
        srcRectWithStroke.inset(inset, inset)
        matrix.setRectToRect(srcRectWithStroke, RectF(bounds), Matrix.ScaleToFit.CENTER)
    }

    override fun draw(canvas: Canvas) {
        canvasWrapper.setCanvas(canvas)
        for (faceIndex in model.facePositions.indices) {
            setMatrixForTargetFace(canvasWrapper, faceIndex)
            for (zone in zonesToDraw) {
                zone.drawFill(canvasWrapper)
            }
        }
        for (faceIndex in model.facePositions.indices) {
            setMatrixForTargetFace(canvasWrapper, faceIndex)
            for (zone in zonesToDraw) {
                zone.drawStroke(canvasWrapper)
            }
            onPostDraw(canvasWrapper, faceIndex)
        }
        canvasWrapper.releaseCanvas()
    }

    private fun setMatrixForTargetFace(canvas: CanvasWrapper, faceIndex: Int) {
        canvas.setMatrix(getTargetFaceMatrix(faceIndex))
    }

    private fun getTargetFaceMatrix(faceIndex: Int): Matrix {
        val m = drawMatrices[faceIndex]
        m.set(getPreCalculatedFaceMatrix(faceIndex))
        m.postConcat(spotMatrix)
        m.postTranslate(-px - 1, -py - 1)
        m.postScale(zoom, zoom)
        m.postTranslate(zoom + px, zoom + py)
        m.postConcat(matrix)
        m.postTranslate(xOffset, yOffset)
        return m
    }

    fun getPreCalculatedFaceMatrix(faceIndex: Int): Matrix {
        return targetFaceMatrices[faceIndex]
    }

    private fun calculateTargetFaceMatrix(index: Int): Matrix {
        val pos = model.facePositions[index % model.facePositions.size]
        val matrix = Matrix()
        matrix.setRectToRect(SRC_RECT,
                RectF(pos.x - model.faceRadius, pos.y - model.faceRadius,
                        pos.x + model.faceRadius, pos.y + model.faceRadius),
                Matrix.ScaleToFit.FILL)
        return matrix
    }

    protected open fun onPostDraw(canvas: CanvasWrapper, faceIndex: Int) {
        model.decorator?.drawDecoration(canvas)
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(arg0: Int) {}

    override fun setColorFilter(arg0: ColorFilter?) {}

    fun setZoom(zoom: Float) {
        this.zoom = zoom
    }

    fun setMid(px: Float, py: Float) {
        this.px = px
        this.py = py
    }

    fun setOffset(x: Float, y: Float) {
        xOffset = x
        yOffset = y
    }

    companion object {
        val SRC_RECT = RectF(-1f, -1f, 1f, 1f)
    }
}
