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
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE
import de.dreier.mytargets.shared.utils.Color.WHITE

class NFAAIndoor5Spot : TargetModelBase(
        id = ID,
        nameRes = R.string.nfaa_indoor_5_spot,
        zones = arrayOf(
                CircularZone(0.25f, WHITE, DARK_GRAY, 2),
                CircularZone(0.5f, WHITE, DARK_GRAY, 2),
                CircularZone(0.75f, SAPPHIRE_BLUE, WHITE, 2),
                CircularZone(1.0f, SAPPHIRE_BLUE, WHITE, 0)
        ),
        scoringStyles = arrayOf(
                ScoringStyle(true, 5, 5, 4, 4),
                ScoringStyle(false, 6, 6, 5, 4),
                ScoringStyle(false, 7, 6, 5, 4)
        ),
        diameters = arrayOf(Dimension(40f, CENTIMETER))
) {
    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 25f, 9, true)
        facePositions = arrayOf(
                PointF(-0.6f, -0.6f),
                PointF(0.6f, -0.6f),
                PointF(0.0f, 0.0f),
                PointF(-0.6f, 0.6f),
                PointF(0.6f, 0.6f)
        )
        faceRadius = 0.4f
    }

    companion object {
        val ID = 11L
    }
}
