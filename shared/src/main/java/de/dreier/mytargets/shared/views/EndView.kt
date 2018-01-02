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

package de.dreier.mytargets.shared.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

import com.evernote.android.state.State
import com.evernote.android.state.StateSaver

import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.utils.EndRenderer

class EndView : View {

    private var shotCount: Int = 0
    private var density: Float = 0.toFloat()
    @State
    var endRenderer = EndRenderer()
    private val rect = RectF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setShots(target: Target, shots: List<Shot>) {
        shotCount = shots.size
        density = resources.displayMetrics.density
        endRenderer.init(this, density, target)
        endRenderer.setShots(shots)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        endRenderer.draw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.left = 0f
        rect.right = width.toFloat()
        rect.top = 0f
        rect.bottom = height.toFloat()
        endRenderer.setRect(rect)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (60f * shotCount.toFloat() * density).toInt()
        val desiredHeight = (50 * density).toInt()

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }

        height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightSize
            View.MeasureSpec.AT_MOST -> Math.min(desiredHeight, heightSize)
            View.MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState())
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state))
    }

    fun setAmbientMode(ambient: Boolean) {
        endRenderer.setAmbientMode(ambient)
        invalidate()
    }
}
