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

import android.graphics.Rect

object RectUtils {
    fun fitRectWithin(inner: Rect, outer: Rect): Rect {
        val innerAspectRatio = inner.width() / inner.height().toFloat()
        val outerAspectRatio = outer.width() / outer.height().toFloat()

        val resizeFactor = if (innerAspectRatio >= outerAspectRatio)
            outer.width() / inner.width().toFloat()
        else
            outer.height() / inner.height().toFloat()

        val newWidth = inner.width() * resizeFactor
        val newHeight = inner.height() * resizeFactor
        val newLeft = outer.left + (outer.width() - newWidth) / 2f
        val newTop = outer.top + (outer.height() - newHeight) / 2f

        return Rect(newLeft.toInt(), newTop.toInt(), (newWidth + newLeft).toInt(), (newHeight + newTop).toInt())
    }
}
