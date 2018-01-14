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

class WAField3Spot : WAField(ID, R.string.wa_field_3_spot) {
    init {
        faceRadius = 0.32f
        facePositions = arrayOf(
                PointF(0.0f, -0.68f),
                PointF(0.0f, 0.0f),
                PointF(0.0f, 0.68f)
        )
    }

    override val singleSpotTargetId: Long
        get() = WAField.ID

    companion object {
        const val ID = 25L
    }
}
