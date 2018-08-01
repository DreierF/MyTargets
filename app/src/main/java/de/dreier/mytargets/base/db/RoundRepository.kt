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

import androidx.room.RoomDatabase
import de.dreier.mytargets.base.db.dao.EndDAO
import de.dreier.mytargets.base.db.dao.RoundDAO
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.Round

class RoundRepository(val database: AppDatabase) {

    private val roundDAO: RoundDAO = database.roundDAO()
    private val endDAO: EndDAO = database.endDAO()
    private val endRepository = EndRepository(endDAO)

    fun loadAugmentedRound(id: Long) =
        AugmentedRound(roundDAO.loadRound(id), endRepository.loadAugmentedEnds(id))

    fun loadAugmentedRound(round: Round) =
        AugmentedRound(round, endRepository.loadAugmentedEnds(round.id))

    fun insertRound(round: AugmentedRound) {
        database.runInTransaction {
            roundDAO.insertRound(round.round, round.ends.map { it.end })
            round.ends.forEach {
                endDAO.insertCompleteEnd(it.end, it.images, it.shots)
            }
        }
    }
}
