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

package de.dreier.mytargets.shared.targets.scoringstyle

class ArrowAwareScoringStyle(showAsX: Boolean, points: Array<IntArray>) : ScoringStyle(showAsX, points) {

    override fun getPoints(zone: Int, arrow: Int): Int {
        return points[if (arrow < points.size) arrow else points.size - 1][zone]
    }
}
