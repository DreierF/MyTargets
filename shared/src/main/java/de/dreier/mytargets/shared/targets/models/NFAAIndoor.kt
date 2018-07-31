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
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE
import de.dreier.mytargets.shared.utils.Color.WHITE

class NFAAIndoor : TargetModelBase(
        id = ID,
        nameRes = R.string.nfaa_indoor,
        zones = listOf(
                CircularZone(0.1f, WHITE, DARK_GRAY, 4),
                CircularZone(0.2f, WHITE, DARK_GRAY, 0),
                CircularZone(0.4f, SAPPHIRE_BLUE, WHITE, 4),
                CircularZone(0.6f, SAPPHIRE_BLUE, WHITE, 4),
                CircularZone(0.8f, SAPPHIRE_BLUE, WHITE, 4),
                CircularZone(1.0f, SAPPHIRE_BLUE, WHITE, 4)
        ),
        scoringStyles = listOf(
                ScoringStyle(true, 5, 5, 4, 3, 2, 1),
                ScoringStyle(false, 6, 5, 4, 3, 2, 1),
                ScoringStyle(false, 7, 5, 4, 3, 2, 1)
        ),
        diameters = listOf(Dimension(40f, CENTIMETER))
) {
    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 23.783f, 8, true)
    }

    companion object {
        const val ID = 10L
    }

}
