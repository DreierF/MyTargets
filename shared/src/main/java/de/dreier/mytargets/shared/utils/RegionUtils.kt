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

import android.graphics.*

object RegionUtils {

    fun getScaledRegion(path: Path, scale: Float): Region {
        // Scale the path
        val scaledPath = Path(path)
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        scaledPath.transform(matrix)

        // Get path bounds
        val bounds = RectF()
        scaledPath.computeBounds(bounds, true)
        val rectBounds = Rect()
        bounds.roundOut(rectBounds)

        // Create region from path
        val region = Region()
        region.setPath(scaledPath, Region(rectBounds))
        return region
    }
}
