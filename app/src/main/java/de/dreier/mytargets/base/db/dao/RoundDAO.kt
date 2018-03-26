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

package de.dreier.mytargets.base.db.dao

import android.arch.persistence.room.*
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round

@Dao
abstract class RoundDAO {
    @Query("SELECT * FROM Round WHERE id in (:roundIds)")
    abstract fun loadRounds(roundIds: LongArray): List<Round>

    @Query("SELECT * FROM Round WHERE trainingId = :id ORDER BY `index`")
    abstract fun loadRounds(id: Long): List<Round>

    @Query("SELECT * FROM Round WHERE id = :id")
    abstract fun loadRound(id: Long): Round

    @Query("SELECT * FROM `Round` WHERE `id` = :id")
    abstract fun loadRoundOrNull(id: Long): Round?

    @Query("SELECT * FROM `End` WHERE `roundId` = :id ORDER BY `index`")
    abstract fun loadEnds(id: Long): MutableList<End>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRound(round: Round): Long

    @Insert
    abstract fun insertEnds(round: List<End>)

    @Update
    abstract fun updateRound(round: Round)

    @Query("DELETE FROM `End` WHERE `roundId` = :id")
    abstract fun deleteEnds(id: Long)

    @Transaction
    open fun saveRound(round: Round, ends: List<End>) {
        round.id = insertRound(round)
        deleteEnds(round.id)
        for (end in ends) {
            end.roundId = round.id
        }
        insertEnds(ends)
    }

    @Transaction
    open fun deleteRound(round: Round) {
        deleteRound(round)
        decrementIndices(round.index)
    }

    @Query("UPDATE Round SET `index` = `index` - 1 WHERE `index` > :allAboveIndex")
    abstract fun decrementIndices(allAboveIndex: Int)

    @Query("UPDATE Round SET `index` = `index` + 1 WHERE `index` >= :allAboveIndex")
    abstract fun incrementIndices(allAboveIndex: Int)

    @Transaction
    open fun insertRound(round: Round, ends: List<End>) {
        incrementIndices(round.index)
        saveRound(round, ends)
    }
}
