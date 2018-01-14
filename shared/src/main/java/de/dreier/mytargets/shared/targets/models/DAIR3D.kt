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
import de.dreier.mytargets.shared.targets.zone.HeartZone
import de.dreier.mytargets.shared.utils.Color.BLACK
import de.dreier.mytargets.shared.utils.Color.BROWN
import de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE
import de.dreier.mytargets.shared.utils.Color.GRAY
import de.dreier.mytargets.shared.utils.Color.GREEN
import de.dreier.mytargets.shared.utils.Color.LIGHT_GRAY
import de.dreier.mytargets.shared.utils.Color.RED
import de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW

class DAIR3D : Target3DBase(
        id = ID,
        nameRes = R.string.dair_3d,
        zones = arrayOf(
                CircularZone(0.053396f, -0.631104f, -0.49886402f, GREEN, BLACK, 3),
                CircularZone(0.124f, -0.631104f, -0.49886402f, GREEN, BLACK, 3),
                CircularZone(0.053396f, 0.274396f, -0.089198f, TURBO_YELLOW, BLACK, 3),
                CircularZone(0.053396f, -0.017104004f, 0.399802f, TURBO_YELLOW, BLACK, 3),
                CircularZone(0.053396f, 0.12689404f, 0.15313196f, RED, BLACK, 3),
                CircularZone(0.124f, 0.12689404f, 0.15313196f, RED, BLACK, 3),
                CircularZone(0.124f, 0.274396f, -0.089198f, TURBO_YELLOW, BLACK, 3),
                CircularZone(0.124f, -0.017104004f, 0.399802f, TURBO_YELLOW, BLACK, 3),
                CircularZone(0.417876f, 0.12875f, 0.15562597f, CERULEAN_BLUE, BLACK, 4),
                HeartZone(1.0f, 0.0f, 0.0f, LIGHT_GRAY, BLACK, 3),
                CircularZone(1.0f, 0.0f, 0.0f, BROWN, GRAY, 5)
        ),
        scoringStyles = arrayOf(
                ScoringStyle(false, 8, 8, 12, 12, 12, 12, 11, 11, 10, 8, 0),
                ScoringStyle(false, 14, 14, 12, 12, 12, 12, 11, 11, 10, 8, 0),
                ScoringStyle(false, 8, 8, 12, 12, 12, 12, 10, 10, 10, 8, 0),
                ScoringStyle(false, 14, 14, 12, 12, 12, 12, 10, 10, 10, 8, 0),
                ScoringStyle(false, 14, 14, 11, 11, 10, 10, 10, 10, 10, 8, 5)
        )
) {
    companion object {
        const val ID = 19L
    }
}
