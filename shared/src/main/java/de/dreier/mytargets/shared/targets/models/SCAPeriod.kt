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
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.GREEN
import de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW
import de.dreier.mytargets.shared.utils.Color.WHITE

class SCAPeriod : TargetModelBase(
        id = ID,
        nameRes = R.string.sca_period,
        zones = listOf(
                CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 2),
                CircularZone(0.4f, GREEN, DARK_GRAY, 2),
                CircularZone(1.0f, WHITE, DARK_GRAY, 2)
        ),
        scoringStyles = listOf(ScoringStyle(false, 8, 4, 2)),
        diameters = listOf(Dimension(60f, Dimension.Unit.CENTIMETER))
) {
    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 5f, 4, false)
    }

    companion object {
        private const val ID = 24L
    }
}
