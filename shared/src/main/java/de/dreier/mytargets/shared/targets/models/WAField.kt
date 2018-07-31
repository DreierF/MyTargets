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

import android.graphics.Color.WHITE
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.utils.Color.DARK_GRAY
import de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW

open class WAField internal constructor(id: Long, nameRes: Int) : TargetModelBase(
        id = id,
        nameRes = nameRes,
        zones = listOf(
                CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 4),
                CircularZone(0.2f, LEMON_YELLOW, DARK_GRAY, 4),
                CircularZone(0.4f, DARK_GRAY, WHITE, 4),
                CircularZone(0.6f, DARK_GRAY, WHITE, 4),
                CircularZone(0.8f, DARK_GRAY, WHITE, 4),
                CircularZone(1.0f, DARK_GRAY, WHITE, 4)
        ),
        scoringStyles = listOf(
                ScoringStyle(true, 5, 5, 4, 3, 2, 1),
                ScoringStyle(false, 6, 5, 4, 3, 2, 1)
        ),
        diameters = listOf(
                Dimension(20f, CENTIMETER),
                Dimension(40f, CENTIMETER),
                Dimension(60f, CENTIMETER),
                Dimension(80f, CENTIMETER)
        ),
        type = ETargetType.FIELD
) {

    constructor() : this(ID, R.string.wa_field)

    init {
        decorator = CenterMarkDecorator(DARK_GRAY, 10.5f, 4, false)
    }

    companion object {
        const val ID = 13L
    }
}
