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

package de.dreier.mytargets.shared.targets.zone

import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper
import de.dreier.mytargets.shared.utils.RegionUtils

class EllipseZone(
        radius: Float,
        midpointX: Float,
        midpointY: Float,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int
) : ZoneBase(radius, PointF(midpointX, midpointY), fillColor, strokeColor, strokeWidth, true) {

    override fun isInZone(ax: Float, ay: Float, arrowRadius: Float): Boolean {
        return ELLIPSE_REGION
                .contains((ax * REGION_SCALE_FACTOR).toInt(), (ay * REGION_SCALE_FACTOR).toInt())
    }

    override fun drawFill(canvas: CanvasWrapper) {
        canvas.drawPath(ellipse, paintFill)
    }

    override fun drawStroke(canvas: CanvasWrapper) {
        canvas.drawPath(ellipse, paintStroke)
    }

    companion object {
        private const val REGION_SCALE_FACTOR = 1000f
        private val ELLIPSE_REGION: Region
        private val ellipse = Path()

        init {
            ellipse.moveTo(0.34f, -0.495f)
            ellipse.arcTo(RectF(-0.811f, -0.495f, 0.139f, 0.499f), -90f, -180f, false)
            ellipse.arcTo(RectF(-0.135f, -0.495f, 0.815f, 0.499f), 90f, -180f, false)
            ellipse.close()

            /** The region needs to be bigger, because the Region#contains(x,y) only allows to test for
             * integers, which is obviously to inaccurate for a -1..1 coordinate system.  */
            ELLIPSE_REGION = RegionUtils.getScaledRegion(ellipse, REGION_SCALE_FACTOR)
        }
    }
}
