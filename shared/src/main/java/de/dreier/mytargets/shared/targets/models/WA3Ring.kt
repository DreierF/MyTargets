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
import de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED
import de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW

open class WA3Ring internal constructor(id: Long, nameRes: Int, diameters: Array<Dimension>) : TargetModelBase(
        id = id,
        nameRes = nameRes,
        zones = arrayOf(
                CircularZone(0.166f, LEMON_YELLOW, DARK_GRAY, 4),
                CircularZone(0.334f, LEMON_YELLOW, DARK_GRAY, 4),
                CircularZone(0.666f, LEMON_YELLOW, DARK_GRAY, 4),
                CircularZone(1.0f, FLAMINGO_RED, DARK_GRAY, 4)
        ),
        scoringStyles = arrayOf(
                ScoringStyle(R.string.recurve_style_x_8, true, 10, 10, 9, 8),
                ScoringStyle(R.string.recurve_style_10_8, false, 10, 10, 9, 8),
                ScoringStyle(R.string.compound_style, false, 10, 9, 9, 8),
                ScoringStyle(false, 11, 10, 9, 8),
                ScoringStyle(true, 5, 5, 5, 4),
                ScoringStyle(false, 9, 9, 9, 7)
        ),
        diameters = diameters
) {

    constructor() : this(
            id = ID,
            nameRes = R.string.wa_3_ring,
            diameters = arrayOf(
                    Dimension(40f, CENTIMETER),
                    Dimension(60f, CENTIMETER),
                    Dimension(80f, CENTIMETER),
                    Dimension(92f, CENTIMETER),
                    Dimension(122f, CENTIMETER)
            )
    )

    init {
        realSizeFactor = 0.3f
        decorator = CenterMarkDecorator(DARK_GRAY, 16.667f, 4, false)
    }

    override fun shouldDrawZone(zone: Int, scoringStyle: Int): Boolean {
        // Do not draw second ring if we have a compound face
        return !(scoringStyle == 1 && zone == 0) && !(scoringStyle == 2 && zone == 1)
    }

    companion object {
        const val ID = 3L
    }
}
