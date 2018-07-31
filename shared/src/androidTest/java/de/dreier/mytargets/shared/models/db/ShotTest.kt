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

package de.dreier.mytargets.shared.models.db

import junit.framework.Assert
import org.junit.Test
import java.util.*

class ShotTest {

    @Test
    @Throws(Exception::class)
    fun testCompareTo() {
        Assert.assertEquals("0,1,-1,-2",
                toSortedShotList(Shot.NOTHING_SELECTED, Shot.MISS, 0, 1))
        Assert.assertEquals("0,2,-1,-2",
                toSortedShotList(0, 2, Shot.MISS, Shot.NOTHING_SELECTED))
    }

    private fun toSortedShotList(vararg zones: Int): String {
        val shots = ArrayList<Shot>(zones.size)
        for (i in zones.indices) {
            shots.add(Shot(i))
            shots[i].scoringRing = zones[i]
        }
        shots.sort()
        return shots.joinToString(",") { it.scoringRing.toString() }
    }
}
