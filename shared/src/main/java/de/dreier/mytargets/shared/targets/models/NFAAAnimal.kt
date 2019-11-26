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
import de.dreier.mytargets.shared.models.Diameter
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.targets.scoringstyle.ArrowAwareScoringStyle
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.targets.zone.EllipseZone
import de.dreier.mytargets.shared.utils.Color.BLACK
import de.dreier.mytargets.shared.utils.Color.GRAY
import de.dreier.mytargets.shared.utils.Color.LIGHTER_GRAY
import de.dreier.mytargets.shared.utils.Color.ORANGE
import de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW

class NFAAAnimal : TargetModelBase(
        id = ID,
        nameRes = R.string.nfaa_animal,
        diameters = listOf(Diameter.SMALL, Diameter.MEDIUM, Diameter.LARGE, Diameter.XLARGE),
        zones = listOf(
                CircularZone(0.162f, TURBO_YELLOW, BLACK, 5),
                EllipseZone(1.0f, 0.0f, 0.0f, ORANGE, BLACK, 4),
                CircularZone(1.0f, LIGHTER_GRAY, GRAY, 3)
        ),
        scoringStyles = listOf(
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(21, 20, 18), intArrayOf(17, 16, 14), intArrayOf(13, 12, 10))),
                ScoringStyle(false, intArrayOf(20, 16, 10)),
                ScoringStyle(false, intArrayOf(15, 12, 7)),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(20, 18, 16), intArrayOf(14, 12, 10), intArrayOf(8, 6, 4))),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(20, 18, 16), intArrayOf(12, 10, 8), intArrayOf(6, 4, 2))),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(15, 10, 5), intArrayOf(12, 7, 2)))
        ),
        type = ETargetType.THREE_D
) {
    companion object {
        const val ID = 21L
    }
}
