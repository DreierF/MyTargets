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
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.targets.zone.EllipseZone
import de.dreier.mytargets.shared.utils.Color.BLACK
import de.dreier.mytargets.shared.utils.Color.GRAY
import de.dreier.mytargets.shared.utils.Color.LIGHTER_GRAY
import de.dreier.mytargets.shared.utils.Color.ORANGE

class IFAAAnimal : TargetModelBase(
        id = ID,
        nameRes = R.string.ifaa_animal,
        diameters = listOf(Diameter.SMALL, Diameter.MEDIUM, Diameter.LARGE, Diameter.XLARGE),
        zones = listOf(
                EllipseZone(1.0f, 0.0f, 0.0f, ORANGE, BLACK, 4),
                CircularZone(1.0f, LIGHTER_GRAY, GRAY, 3)
        ),
        scoringStyles = listOf(
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(20, 18), intArrayOf(16, 14), intArrayOf(12, 10))),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(20, 15), intArrayOf(15, 10))),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(20, 16), intArrayOf(14, 10), intArrayOf(8, 4))),
                ArrowAwareScoringStyle(false, arrayOf(intArrayOf(15, 12), intArrayOf(10, 7), intArrayOf(5, 2)))
        ),
        type = ETargetType.THREE_D
) {
    companion object {
        const val ID = 20L
    }
}
