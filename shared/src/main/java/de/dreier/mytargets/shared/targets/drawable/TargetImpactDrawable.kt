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

import android.graphics.Paint
import android.graphics.RectF
import android.os.AsyncTask
import android.text.TextPaint
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.utils.Color.WHITE
import java.util.*

open class TargetImpactDrawable(target: Target) : TargetDrawable(target) {
    protected var shots: MutableList<MutableList<Shot>> = ArrayList()
    protected var transparentShots: MutableList<MutableList<Shot>> = ArrayList()
    private var arrowRadius: Float = 0.toFloat()
    private var shouldDrawArrows = true
    private var focusedArrow: Shot? = null
    private val paintText: TextPaint by lazy {
        val paintText = TextPaint()
        paintText.isAntiAlias = true
        paintText.color = WHITE
        paintText
    }
    private val paintFill: Paint by lazy {
        val paintFill = Paint()
        paintFill.isAntiAlias = true
        paintFill
    }

    init {
        setArrowDiameter(Dimension(5f, Dimension.Unit.MILLIMETER), 1f)
        for (i in 0 until model.faceCount) {
            shots.add(ArrayList())
            transparentShots.add(ArrayList())
        }
    }

    fun setArrowDiameter(arrowDiameter: Dimension, scale: Float) {
        val (value) = model.getRealSize(target.diameter!!).convertTo(arrowDiameter.unit!!)
        arrowRadius = arrowDiameter.value * scale / value
    }

    override fun onPostDraw(canvas: CanvasWrapper, faceIndex: Int) {
        super.onPostDraw(canvas, faceIndex)
        if (!shouldDrawArrows) {
            return
        }

        if (transparentShots.size > faceIndex) {
            for (s in transparentShots[faceIndex]) {
                drawArrow(canvas, s, true)
            }
        }
        if (shots.size > faceIndex) {
            for (s in shots[faceIndex]) {
                drawArrow(canvas, s, false)
            }
        }
        if (focusedArrow != null) {
            drawFocusedArrow(canvas, focusedArrow!!, faceIndex)
        }
    }

    fun getZoneFromPoint(x: Float, y: Float): Int {
        return model.getZoneFromPoint(x, y, arrowRadius)
    }

    private fun drawArrow(canvas: CanvasWrapper, shot: Shot, transparent: Boolean) {
        var color = model.getContrastColor(shot.scoringRing)
        if (transparent) {
            color = 0x55000000 or (color and 0xFFFFFF)
        }
        paintFill.color = color
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill)
    }

    fun setFocusedArrow(shot: Shot?) {
        focusedArrow = shot
        if (shot == null) {
            setMid(0f, 0f)
        } else {
            setMid(shot.x, shot.y)
        }
    }

    private fun drawFocusedArrow(canvas: CanvasWrapper, shot: Shot, drawFaceIndex: Int) {
        if (shot.index % model.faceCount != drawFaceIndex) {
            return
        }

        paintFill.color = -0xff6700
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill)

        // Draw cross
        val lineLen = 2f * arrowRadius
        paintFill.strokeWidth = 0.2f * arrowRadius
        canvas.drawLine(shot.x - lineLen, shot.y, shot.x + lineLen, shot.y, paintFill)
        canvas.drawLine(shot.x, shot.y - lineLen, shot.x, shot.y + lineLen, paintFill)

        // Draw zone points
        val zoneString = target.zoneToString(shot.scoringRing, shot.index)
        val srcRect = RectF(shot.x - arrowRadius, shot.y - arrowRadius,
                shot.x + arrowRadius, shot.y + arrowRadius)
        canvas.drawText(zoneString, srcRect, paintText)
    }

    fun replaceShotsWith(shots: List<Shot>) {
        for (i in this.shots.indices) {
            this.shots[i].clear()
        }
        val map = shots.groupBy { (_, index) -> index % model.faceCount }
        for ((key, value) in map) {
            this.shots[key] = value.toMutableList()
        }
        notifyArrowSetChanged()
    }

    fun replacedTransparentShots(shots: List<Shot>) {
        object : AsyncTask<Void, Void, Map<Int, List<Shot>>>() {
            override fun doInBackground(vararg objects: Void): Map<Int, List<Shot>> {
                return shots.groupBy { (_, index) -> index % model.faceCount }
            }

            override fun onPostExecute(map: Map<Int, List<Shot>>) {
                super.onPostExecute(map)
                for (shotList in transparentShots) {
                    shotList.clear()
                }
                for ((key, value) in map) {
                    transparentShots[key] = value.toMutableList()
                }
                notifyArrowSetChanged()
            }
        }.execute()
    }

    open fun notifyArrowSetChanged() {
        invalidateSelf()
    }

    fun drawArrowsEnabled(enabled: Boolean) {
        shouldDrawArrows = enabled
    }

    open fun cleanup() {

    }
}
