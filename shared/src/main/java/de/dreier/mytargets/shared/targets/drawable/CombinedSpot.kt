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

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

class CombinedSpot(private val faces: List<TargetDrawable>) : Drawable() {
    private val faceRect = Rect()

    override fun draw(canvas: Canvas) {
        val rect = bounds
        val faceRadius = Math.min(rect.width() * 1.2 / faces.size, (rect.width() / 2).toDouble()).toInt()
        val x = (rect.width() - faceRadius * 2) / Math.max(faces.size - 1, 1)
        for (i in faces.indices) {
            faceRect.left = x * i
            faceRect.top = x * i
            faceRect.right = faceRect.left + faceRadius * 2
            faceRect.bottom = faceRect.top + faceRadius * 2
            faces[i].bounds = faceRect
            faces[i].draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}
