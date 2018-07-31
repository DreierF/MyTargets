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

package de.dreier.mytargets.shared.utils

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * A Drawable that draws an oval with given [Bitmap]
 */
class RoundedAvatarDrawable(bitmap: Bitmap) : Drawable() {
    private val mPaint: Paint = Paint()
    private val mRectF: RectF = RectF()
    private val mBitmapWidth: Int = bitmap.width
    private val mBitmapHeight: Int = bitmap.height

    init {
        mPaint.isAntiAlias = true
        mPaint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawOval(mRectF, mPaint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mRectF.set(bounds)
    }

    override fun setAlpha(alpha: Int) {
        if (mPaint.alpha != alpha) {
            mPaint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return mBitmapWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mBitmapHeight
    }

    override fun setFilterBitmap(filter: Boolean) {
        mPaint.isFilterBitmap = filter
        invalidateSelf()
    }
}
