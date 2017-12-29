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
import de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED
import de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW

class WA6Ring : TargetModelBase(
        id = ID,
        nameRes = R.string.wa_6_ring,
        zones = arrayOf(
                CircularZone(0.084f, LEMON_YELLOW, DARK_GRAY, 3),
                CircularZone(0.166f, LEMON_YELLOW, DARK_GRAY, 3),
                CircularZone(0.334f, LEMON_YELLOW, DARK_GRAY, 3),
                CircularZone(0.5f, FLAMINGO_RED, DARK_GRAY, 3),
                CircularZone(0.666f, FLAMINGO_RED, DARK_GRAY, 3),
                CircularZone(0.834f, CERULEAN_BLUE, DARK_GRAY, 3),
                CircularZone(1.0f, CERULEAN_BLUE, DARK_GRAY, 3)
        ),
        scoringStyles = arrayOf(
                ScoringStyle(R.string.recurve_style_x_5, true, 10, 10, 9, 8, 7, 6, 5),
                ScoringStyle(R.string.recurve_style_10_5, false, 10, 10, 9, 8, 7, 6, 5),
                ScoringStyle(R.string.compound_style, false, 10, 9, 9, 8, 7, 6, 5),
                ScoringStyle(false, 11, 10, 9, 8, 7, 6, 5),
                ScoringStyle(true, 5, 5, 5, 4, 4, 3, 3),
                ScoringStyle(false, 9, 9, 9, 7, 7, 5, 5)
        ),
        diameters = arrayOf(
                Dimension(40f, CENTIMETER),
                Dimension(60f, CENTIMETER),
                Dimension(80f, CENTIMETER),
                Dimension(92f, CENTIMETER),
                Dimension(122f, CENTIMETER)
        )
) {
    init {
        realSizeFactor = 0.6f
        decorator = CenterMarkDecorator(DARK_GRAY, 8.333f, 4, false)
    }

    override fun shouldDrawZone(zone: Int, scoringStyle: Int): Boolean {
        // Do not draw second ring if we have a compound face
        return !(scoringStyle == 1 && zone == 0) && !(scoringStyle == 2 && zone == 1)
    }

    companion object {
        const val ID = 1L
    }
}
