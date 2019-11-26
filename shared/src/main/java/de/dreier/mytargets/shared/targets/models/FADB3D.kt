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
import de.dreier.mytargets.shared.targets.Target3DBase
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.targets.zone.EllipseZone
import de.dreier.mytargets.shared.targets.zone.HeartZone
import de.dreier.mytargets.shared.utils.Color.BLACK
import de.dreier.mytargets.shared.utils.Color.BROWN
import de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE
import de.dreier.mytargets.shared.utils.Color.GRAY
import de.dreier.mytargets.shared.utils.Color.RED

class FADB3D : Target3DBase(
    id = 29,
    nameRes = R.string.fadb_3d,
    zones = listOf(
        CircularZone(0.1f, 0.12689404f, 0.15313196f, RED, BLACK, 3),
        CircularZone(0.22f, 0.12689404f, 0.15313196f, RED, BLACK, 3),
        HeartZone(0.5f, 0.0f, 0.0f, CERULEAN_BLUE, BLACK, 3),
        CircularZone(1.0f, 0.0f, 0.0f, BROWN, GRAY, 5)
    ),
    scoringStyles = listOf(
        ScoringStyle(false, -1, intArrayOf(5, 5, 3, -1)))
)
