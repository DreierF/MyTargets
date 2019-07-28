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
package de.dreier.mytargets.shared.targets

import androidx.annotation.StringRes
import de.dreier.mytargets.shared.models.Diameter
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.targets.models.TargetModelBase
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.ZoneBase

abstract class Target3DBase protected constructor(
        id: Long,
        @StringRes nameRes: Int,
        zones: List<ZoneBase>,
        scoringStyles: List<ScoringStyle>
) : TargetModelBase(
        id = id,
        nameRes = nameRes,
        diameters = listOf(Diameter.MINI, Diameter.SMALL, Diameter.MEDIUM, Diameter.LARGE, Diameter.XLARGE),
        type = ETargetType.THREE_D,
        zones = zones,
        scoringStyles = scoringStyles
)
