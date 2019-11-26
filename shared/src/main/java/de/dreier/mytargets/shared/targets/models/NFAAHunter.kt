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

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.WHITE

class NFAAHunter : TargetModelBase(
        id = ID,
        nameRes = R.string.nfaa_hunter,
        zones = listOf(
                CircularZone(0.1f, WHITE, DARK_GRAY, 4),
                CircularZone(0.2f, WHITE, WHITE, 0),
                CircularZone(0.6f, DARK_GRAY, WHITE, 4),
                CircularZone(1.0f, DARK_GRAY, DARK_GRAY, 0)
        ),
        scoringStyles = listOf(
                ScoringStyle(true, intArrayOf(5, 5, 4, 3)),
                ScoringStyle(false, intArrayOf(6, 5, 4, 3))
        ),
        diameters = listOf(
                Dimension(20f, CENTIMETER),
                Dimension(35f, CENTIMETER),
                Dimension(50f, CENTIMETER),
                Dimension(65f, CENTIMETER)
        ),
        type = ETargetType.FIELD
) {
    init {
        decorator = CenterMarkDecorator(WHITE, 7.307f, 4, true)
    }

    companion object {
        const val ID = 9L
    }
}
