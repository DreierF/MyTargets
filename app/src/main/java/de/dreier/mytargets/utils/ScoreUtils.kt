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

package de.dreier.mytargets.utils

import android.support.v4.util.Pair
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.dao.EndDAO
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import java.util.*

object ScoreUtils {

    fun getTopScoreDistribution(sortedScore: List<Map.Entry<SelectableZone, Int>>): List<Pair<String, Int>> {
        val result = sortedScore.map { Pair(it.key.text, it.value) }.toMutableList()

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size > 1) {
            val first = sortedScore[0]
            val second = sortedScore[1]
            if (first.key.points == second.key.points) {
                val newTitle = second.key.text + "+" + first.key.text
                result[1] = Pair(newTitle, second.value + first.value)
            }
        }
        return result
    }

    /**
     * Compound 9ers are already collapsed to one SelectableZone.
     */
    fun getSortedScoreDistribution(rounds: List<Round>): List<Map.Entry<SelectableZone, Int>> {
        return getRoundScores(rounds).entries.sortedBy { it.key }
    }

    private fun getRoundScores(rounds: List<Round>): Map<SelectableZone, Int> {
        val t = rounds[0].target
        val scoreCount = getAllPossibleZones(t)
        rounds.flatMap { RoundDAO.loadEnds(it.id) }
                .forEach {
                    EndDAO.loadShots(it.id).forEach { s ->
                                if (s.scoringRing != Shot.NOTHING_SELECTED) {
                                    val tuple = SelectableZone(s.scoringRing,
                                            t.model.getZone(s.scoringRing),
                                            t.zoneToString(s.scoringRing, s.index),
                                            t.getScoreByZone(s.scoringRing, s.index))
                                    val integer = scoreCount[tuple]
                                    if (integer != null) {
                                        val count = integer + 1
                                        scoreCount[tuple] = count
                                    }
                                }
                            }
                }
        return scoreCount
    }

    private fun getAllPossibleZones(t: Target): MutableMap<SelectableZone, Int> {
        val scoreCount = HashMap<SelectableZone, Int>()
        for (arrow in 0..2) {
            val zoneList = t.getSelectableZoneList(arrow)
            for (selectableZone in zoneList) {
                scoreCount[selectableZone] = 0
            }
            if (!t.model.dependsOnArrowIndex()) {
                break
            }
        }
        return scoreCount
    }
}
