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

package de.dreier.mytargets.base.db

import android.arch.persistence.room.RoomDatabase
import de.dreier.mytargets.base.db.dao.EndDAO
import de.dreier.mytargets.base.db.dao.RoundDAO
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.Round

class RoundRepository(
        private val database: RoomDatabase,
        private val roundDAO: RoundDAO,
        private val endDAO: EndDAO,
        private val endRepository: EndRepository
) {

    fun loadAugmentedRound(id: Long) = AugmentedRound(roundDAO.loadRound(id), endRepository.loadAugmentedEnds(id).toMutableList())

    fun loadAugmentedRound(round: Round) = AugmentedRound(round, endRepository.loadAugmentedEnds(round.id).toMutableList())

    fun insertRound(round: AugmentedRound) {
        roundDAO.insertRound(round.round, round.ends.map { it.end })
        round.ends.forEach {
            endDAO.saveCompleteEnd(it.end, it.images, it.shots)
        }
    }

    fun saveRound(round: AugmentedRound) {
        roundDAO.insertRound(round.round)
        for (end in round.ends) {
            end.end.roundId = round.round.id
            endDAO.saveCompleteEnd(end.end, end.images, end.shots)
        }
    }
}
