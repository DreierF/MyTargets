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
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.DBSC_BLUE
import de.dreier.mytargets.shared.utils.Color.DBSC_RED
import de.dreier.mytargets.shared.utils.Color.DBSC_YELLOW

class DBSCBlowpipe : TargetModelBase(
        id = ID,
        nameRes = R.string.dbsc_blowpipe,
        diameters = arrayOf(Dimension(18f, CENTIMETER)),
        zones = arrayOf(
                CircularZone(0.3333f, DBSC_YELLOW, DARK_GRAY, 8),
                CircularZone(0.6666f, DBSC_RED, DARK_GRAY, 8),
                CircularZone(1f, DBSC_BLUE, DARK_GRAY, 8)
        ),
        scoringStyles = arrayOf(ScoringStyle(false, 7, 5, 3))
) {
    companion object {
        val ID = 28L
    }
}
