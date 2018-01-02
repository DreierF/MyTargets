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

package de.dreier.mytargets.shared.targets.zone

import android.graphics.Path
import android.graphics.PointF
import android.graphics.Region

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper
import de.dreier.mytargets.shared.utils.RegionUtils

class HeartZone(
        radius: Float,
        midpointX: Float,
        midpointY: Float,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int
) : ZoneBase(radius, PointF(midpointX, midpointY), fillColor, strokeColor, strokeWidth, true) {

    override fun isInZone(ax: Float, ay: Float, arrowRadius: Float): Boolean {
        return HEART_REGION
                .contains((ax * REGION_SCALE_FACTOR).toInt(), (ay * REGION_SCALE_FACTOR).toInt())
    }

    override fun drawFill(canvas: CanvasWrapper) {
        canvas.drawPath(heart, paintFill)
    }

    override fun drawStroke(canvas: CanvasWrapper) {
        canvas.drawPath(heart, paintStroke)
    }

    companion object {
        private const val REGION_SCALE_FACTOR = 1000f
        private val HEART_REGION: Region
        private val heart = Path()

        init {
            heart.moveTo(-0.40655202f, -0.67429006f)
            heart.cubicTo(-0.32986602f, -0.67359203f, -0.25351202f, -0.66787f, -0.17732202f, -0.659432f)
            heart.cubicTo(-0.087052f, -0.64943606f, 0.0025360107f, -0.635248f, 0.09162597f, -0.617572f)
            heart.cubicTo(0.20692395f, -0.594698f, 0.31867394f, -0.560058f, 0.42808202f, -0.517558f)
            heart.cubicTo(0.516792f, -0.48309803f, 0.602374f, -0.442294f, 0.681906f, -0.389588f)
            heart.cubicTo(0.75763196f, -0.339404f, 0.82512f, -0.28046802f, 0.87533206f, -0.203838f)
            heart.cubicTo(0.927418f, -0.12434802f, 0.955806f, -0.037151977f, 0.955606f, 0.05826404f)
            heart.cubicTo(0.955458f, 0.12925f, 0.93491393f, 0.19544995f, 0.904044f, 0.25875f)
            heart.cubicTo(0.86593395f, 0.336896f, 0.81412596f, 0.40537193f, 0.7546f, 0.46816796f)
            heart.cubicTo(0.638614f, 0.59052795f, 0.503308f, 0.68716f, 0.35480005f, 0.765842f)
            heart.cubicTo(0.273682f, 0.80881995f, 0.189568f, 0.84470606f, 0.10082605f, 0.869028f)
            heart.cubicTo(0.05665405f, 0.881136f, 0.011823975f, 0.88939196f, -0.034054015f, 0.89117396f)
            heart.cubicTo(-0.08539203f, 0.8931639f, -0.13226202f, 0.877068f, -0.177622f, 0.85528404f)
            heart.cubicTo(-0.25867403f, 0.8163621f, -0.32499403f, 0.758708f, -0.38383198f, 0.691786f)
            heart.cubicTo(-0.45250598f, 0.6136739f, -0.507658f, 0.526442f, -0.554914f, 0.43415198f)
            heart.cubicTo(-0.646068f, 0.256126f, -0.70843f, 0.06803406f, -0.749556f, -0.12738203f)
            heart.cubicTo(-0.764654f, -0.19912598f, -0.77613205f, -0.27143f, -0.782362f, -0.34448603f)
            heart.cubicTo(-0.787078f, -0.399794f, -0.789294f, -0.45522803f, -0.78261596f, -0.510532f)
            heart.cubicTo(-0.780456f, -0.52841794f, -0.776364f, -0.54613405f, -0.772184f, -0.563696f)
            heart.cubicTo(-0.76661795f, -0.58707f, -0.752204f, -0.60421205f, -0.732386f, -0.617384f)
            heart.cubicTo(-0.70046204f, -0.6386f, -0.66463196f, -0.649474f, -0.627708f, -0.65733f)
            heart.cubicTo(-0.57190794f, -0.669202f, -0.515282f, -0.673348f, -0.45836797f, -0.67425f)
            heart.cubicTo(-0.44109803f, -0.674522f, -0.42382202f, -0.67429006f, -0.40655202f, -0.67429006f)
            heart.close()

            /** The region needs to be bigger, because the Region#contains(x,y) only allows to test for
             * integers, which is obviously to inaccurate for a -1..1 coordinate system.  */
            HEART_REGION = RegionUtils.getScaledRegion(heart, REGION_SCALE_FACTOR)
        }
    }
}
