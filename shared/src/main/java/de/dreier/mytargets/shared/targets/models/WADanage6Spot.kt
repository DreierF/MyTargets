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
package de.dreier.mytargets.shared.targets.models

import android.graphics.PointF

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.Dimension

import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER

class WADanage6Spot : WA3Ring(
        id = ID,
        nameRes = R.string.wa_danage_6_spot,
        diameters = arrayOf(
                Dimension(40f, CENTIMETER),
                Dimension(60f, CENTIMETER)
        )
) {
    init {
        faceRadius = 0.38692f
        facePositions = arrayOf(
                PointF(0.0f, -0.61308f),
                PointF(-0.61308f, -0.16767126f),
                PointF(0.6130799f, -0.16767126f),
                PointF(0.0f, 0.03152883f),
                PointF(-0.378922f, 0.5530689f),
                PointF(0.378922f, 0.5530689f)
        )
    }

    override val singleSpotTargetId: Long
        get() = WA3Ring.ID

    companion object {
        val ID = 27L
    }
}
