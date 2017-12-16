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
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.RED_MISS
import de.dreier.mytargets.shared.utils.Color.YELLOW

class HitOrMiss : TargetModelBase(id = 14,
        nameRes = R.string.hit_or_miss,
        zones = arrayOf(
                CircularZone(0.25f, YELLOW, DARK_GRAY, 3),
                CircularZone(1.0f, RED_MISS, DARK_GRAY, 3)
        ),
        scoringStyles = arrayOf(ScoringStyle(false, 1, 0)),
        diameters = arrayOf(Dimension(30f, CENTIMETER), Dimension(96f, CENTIMETER))
) {
    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 4.188f, 4, false)
    }
}
