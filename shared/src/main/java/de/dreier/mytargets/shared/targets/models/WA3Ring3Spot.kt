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
package de.dreier.mytargets.shared.targets.models

import android.graphics.PointF

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.Dimension

import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER

class WA3Ring3Spot : WA3Ring(
        id = ID,
        nameRes = R.string.wa_3_ring_3_spot,
        diameters = listOf(
                Dimension(40f, CENTIMETER),
                Dimension(60f, CENTIMETER)
        )
) {
    init {
        faceRadius = 0.48f
        facePositions = listOf(
                PointF(-0.52f, 0.5f),
                PointF(0.0f, -0.5f),
                PointF(0.52f, 0.5f)
        )
    }

    override val singleSpotTargetId: Long
        get() = WA3Ring.ID

    companion object {
        const val ID = 6L
    }
}
