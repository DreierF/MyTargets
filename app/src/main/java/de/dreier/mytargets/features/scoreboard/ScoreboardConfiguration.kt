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

package de.dreier.mytargets.features.scoreboard

data class ScoreboardConfiguration (
    var showTitle: Boolean = false,
    var showProperties: Boolean = false,
    var showTable: Boolean = false,
    var showStatistics: Boolean = false,
    var showComments: Boolean = false,
    var showPointsColored: Boolean = false,
    var showSignature: Boolean = false
)
