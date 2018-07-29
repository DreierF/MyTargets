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
import de.dreier.mytargets.shared.targets.scoringstyle.ColorScoringStyle
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.BLACK
import de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED
import de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW
import de.dreier.mytargets.shared.utils.Color.WHITE

class WAFull : TargetModelBase(
        id = ID,
        nameRes = R.string.wa_full,
        zones = listOf(
                CircularZone(0.05f, LEMON_YELLOW, DARK_GRAY, 2),
                CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 2),
                CircularZone(0.2f, LEMON_YELLOW, DARK_GRAY, 2),
                CircularZone(0.3f, FLAMINGO_RED, DARK_GRAY, 2),
                CircularZone(0.4f, FLAMINGO_RED, DARK_GRAY, 2),
                CircularZone(0.5f, CERULEAN_BLUE, DARK_GRAY, 2),
                CircularZone(0.6f, CERULEAN_BLUE, DARK_GRAY, 2),
                CircularZone(0.7f, BLACK, DARK_GRAY, 2),
                CircularZone(0.8f, BLACK, DARK_GRAY, 2),
                CircularZone(0.9f, WHITE, DARK_GRAY, 2),
                CircularZone(1.0f, WHITE, DARK_GRAY, 2)
        ),
        scoringStyles = listOf(
                ScoringStyle(R.string.recurve_style_x_1, true, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                ScoringStyle(R.string.recurve_style_10_1, false, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                ScoringStyle(R.string.compound_style, false, 10, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                ScoringStyle(false, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                ScoringStyle(true, 5, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1),
                ScoringStyle(false, 9, 9, 9, 7, 7, 5, 5, 3, 3, 1, 1),
                ColorScoringStyle(R.string.fcfs_color_reversed, 31, 1, 1, 2, 4, 4, 6, 6, 8, 8, 10, 10)
        ),
        diameters = listOf(
                Dimension(40f, CENTIMETER),
                Dimension(60f, CENTIMETER),
                Dimension(80f, CENTIMETER),
                Dimension(92f, CENTIMETER),
                Dimension(122f, CENTIMETER)
        )
) {
    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 5f, 4, false)
    }

    override fun shouldDrawZone(zone: Int, scoringStyle: Int): Boolean {
        // Do not draw second ring if we have a compound face
        return !(scoringStyle == 1 && zone == 0) && !(scoringStyle == 2 && zone == 1)
    }

    companion object {
        const val ID = 0L
    }
}
