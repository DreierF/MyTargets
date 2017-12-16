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

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.WHITE

class NFAAField : TargetModelBase(
        id = ID,
        nameRes = R.string.nfaa_field,
        zones = arrayOf(
                CircularZone(0.1f, DARK_GRAY, WHITE, 4),
                CircularZone(0.2f, DARK_GRAY, DARK_GRAY, 0),
                CircularZone(0.4f, WHITE, DARK_GRAY, 4),
                CircularZone(0.6f, WHITE, WHITE, 0),
                CircularZone(0.8f, DARK_GRAY, WHITE, 4),
                CircularZone(1.0f, DARK_GRAY, DARK_GRAY, 0)
        ),
        scoringStyles = arrayOf(
                ScoringStyle(true, 5, 5, 4, 4, 3, 3),
                ScoringStyle(false, 6, 5, 4, 4, 3, 3)
        ),
        diameters = arrayOf(
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
        val ID = 7L
    }
}
