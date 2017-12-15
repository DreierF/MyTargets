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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.*
import android.os.Parcelable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class EndRenderer(
        private var shotList: List<Shot>? = null,
        private var pressed: Int = NO_SELECTION,
        internal var selected: Int = NO_SELECTION,
        private var selectedPosition: PointF? = null,
        private var selectedRadius: Int = 0,
        private var oldCoordinate: Array<PointF?> = arrayOfNulls(0),
        internal var currentAnimationProgress: Float = -1f,
        private var ambientMode: Boolean = false
) : Parcelable {

    @Transient private var circle: Circle? = null
    @Transient private var parent: View? = null
    @Transient private var rect: RectF? = null
    @Transient private var radius: Int = 0
    @Transient private var grayBackground = Paint()
    @Transient private var density: Float = 0.toFloat()
    @Transient private var shotsPerRow: Int = 0
    @Transient private var rowHeight: Float = 0.toFloat()
    @Transient private var columnWidth: Float = 0.toFloat()
    @Transient private var oldRadius: Int = 0
    @Transient private var oldSelected: Int = 0
    @Transient private var oldSelectedRadius: Int = 0

    private val animator: ValueAnimator
        get() {
            val selectionAnimator = ValueAnimator.ofFloat(0f, 1f)
            selectionAnimator.interpolator = AccelerateDecelerateInterpolator()
            selectionAnimator.addUpdateListener { valueAnimator ->
                currentAnimationProgress = valueAnimator.animatedValue as Float
                parent!!.invalidate()
            }
            selectionAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentAnimationProgress = -1f
                }
            })
            return selectionAnimator
        }

    init {
        grayBackground.color = -0x222223
        grayBackground.isAntiAlias = true
    }

    fun init(parent: View, density: Float, target: Target) {
        this.parent = parent
        this.density = density
        circle = Circle(density, target)
    }

    fun setRect(rect: RectF) {
        this.rect = rect
        if (shotList == null) {
            return
        }
        radius = MAX_CIRCLE_SIZE + MIN_PADDING
        var neededRows: Int
        var maxRows: Int
        do {
            neededRows = Math.ceil((radius.toFloat() * 2f * density * shotList!!.size.toFloat() / rect.width()).toDouble()).toInt()
            maxRows = Math.floor((rect.height() / (radius.toFloat() * 2f * density)).toDouble()).toInt()
            radius--
        } while (neededRows > maxRows)
        radius -= MIN_PADDING
        val numRows = Math.max(neededRows, 1)
        shotsPerRow = Math.ceil((shotList!!.size / numRows.toFloat()).toDouble()).toInt()
        rowHeight = rect.height() / numRows
        columnWidth = rect.width() / shotsPerRow
    }

    fun setShots(shots: List<Shot>) {
        val calcLayout = rect != null && shotList == null
        shotList = shots
        oldCoordinate = arrayOfNulls(shotList!!.size)
        if (calcLayout) {
            setRect(rect!!)
        }
    }

    fun draw(canvas: Canvas) {
        if (rect == null || shotsPerRow == 0) {
            return
        }

        // Draw all points of this end into the given rect
        for (i in shotList!!.indices) {
            val shot = shotList!![i]
            if (shot.scoringRing == Shot.NOTHING_SELECTED) {
                break
            }

            val radius = getRadius(shot)
            val coordinate = getAnimatedPosition(i, shot)
            if (radius > 0) {
                // Draw touch feedback if arrow is pressed
                if (pressed == shot.index) {
                    canvas.drawRect(coordinate.x - radius, coordinate.y - radius,
                            coordinate.x + radius, coordinate.y + radius, grayBackground)
                }

                // Draw circle
                circle!!.draw(canvas, coordinate.x, coordinate.y, shot.scoringRing, radius,
                        shot.index,
                        shot.arrowNumber, ambientMode)
            }
        }
    }

    private fun getPosition(i: Int, shot: Shot): PointF {
        if (selected == shot.index && selectedPosition != null) {
            return PointF(selectedPosition!!.x, selectedPosition!!.y)
        } else {
            val coordinate = PointF()
            val column = i % shotsPerRow + 0.5f
            coordinate.x = rect!!.left + column * columnWidth
            val row = (Math.ceil((i / shotsPerRow).toDouble()) + 0.5).toFloat()
            coordinate.y = rect!!.top + row * rowHeight
            return coordinate
        }
    }

    private fun getAnimatedPosition(i: Int, shot: Shot): PointF {
        val coordinate = getPosition(i, shot)
        val point = oldCoordinate[shot.index]
        if (currentAnimationProgress != -1f && point != null) {
            val oldX = point.x
            val oldY = point.y
            coordinate.x = oldX + (coordinate.x - oldX) * currentAnimationProgress
            coordinate.y = oldY + (coordinate.y - oldY) * currentAnimationProgress
        }
        return coordinate
    }

    private fun getRadius(shot: Shot): Int {
        var rad = radius
        var oRad = oldRadius
        if (selected == shot.index) {
            rad = selectedRadius
        } else if (oldSelected == shot.index) {
            oRad = oldSelectedRadius
        }
        return if (currentAnimationProgress != -1f) {
            (oRad + (rad - oRad) * currentAnimationProgress).toInt()
        } else {
            rad
        }
    }

    fun getAnimationToSelection(selectedShot: Int, c: PointF, radius: Int, rect: RectF?): Animator? {
        if (rect == null) {
            setSelection(selectedShot, c, radius)
            return null
        }
        if (this.rect == null) {
            setRect(rect)
        }
        saveCoordinates()
        setRect(rect)
        setSelection(selectedShot, c, radius)
        return animator
    }

    fun setSelection(selectedShot: Int, c: PointF?, radius: Int) {
        selected = selectedShot
        selectedPosition = c
        selectedRadius = radius
    }

    private fun saveCoordinates() {
        oldSelectedRadius = selectedRadius
        oldRadius = radius
        oldSelected = selected
        for (i in shotList!!.indices) {
            oldCoordinate[shotList!![i].index] = getPosition(i, shotList!![i])
        }
    }

    fun getPressedPosition(x: Float, y: Float): Int {
        if (rect!!.contains(x, y)) {
            val col = Math.floor(((x - rect!!.left) / columnWidth).toDouble()).toInt()
            val row = Math.floor(((y - rect!!.top) / rowHeight).toDouble()).toInt()
            val arrow = row * shotsPerRow + col
            if (arrow < shotList!!.size && shotList!![arrow].scoringRing != Shot.NOTHING_SELECTED) {
                return if (shotList!![arrow].index == selected)
                    -1
                else
                    shotList!![arrow].index
            }
        }
        return -1
    }

    fun setPressed(pressed: Int) {
        if (this.pressed != pressed) {
            this.pressed = pressed
            parent!!.invalidate()
        }
    }

    fun setAmbientMode(ambientMode: Boolean) {
        this.ambientMode = ambientMode
    }

    fun getBoundsForShot(index: Int): Rect {
        val position = getPosition(index, shotList!![index])
        val radius = getRadius(shotList!![index]) * density
        return Rect((position.x - radius).toInt(), (position.y - radius).toInt(),
                (position.x + radius).toInt(), (position.y + radius).toInt())
    }

    companion object {
        val NO_SELECTION = -1
        val MAX_CIRCLE_SIZE = 17
        private val MIN_PADDING = 2
    }
}
