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

package de.dreier.mytargets.shared.models.dao

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.End_Table
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Round_Table

object RoundDAO {
    fun loadRounds(roundIds: LongArray): List<Round> = SQLite.select()
            .from(Round::class.java)
            .where(Round_Table._id.`in`(roundIds.toList()))
            .queryList()

    fun loadRound(id: Long): Round = SQLite.select()
            .from(Round::class.java)
            .where(Round_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("Round $id does not exist")

    fun loadRoundOrNull(id: Long): Round? = SQLite.select()
            .from(Round::class.java)
            .where(Round_Table._id.eq(id))
            .querySingle()

    fun loadEnds(id: Long): MutableList<End> = SQLite.select()
            .from(End::class.java)
            .where(End_Table.round.eq(id))
            .orderBy(End_Table.index, true)
            .queryList().toMutableList()

    fun saveRound(round: Round) {
        round.save()
    }

    fun saveRound(round: Round, ends: List<End>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            saveRound(db, round, ends)
        }
    }

    private fun saveRound(db: DatabaseWrapper, round: Round, ends: List<End>) {
        round.save(db)
        SQLite.delete(End::class.java)
                .where(End_Table.round.eq(round.id))
                .execute(db)
        for (end in ends) {
            end.roundId = round.id
            end.save(db)
        }
    }

    fun deleteRound(round: Round) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            round.delete(db)
            db.execSQL("UPDATE Round SET `index` = `index` - 1 WHERE `index` > ${round.index}")
        }
    }

    fun insertRound(round: AugmentedRound) {
        RoundDAO.insertRound(round.round, round.ends.map { it.end })
        round.ends.forEach {
            EndDAO.saveEnd(it.end, it.images, it.shots)
        }
    }

    private fun insertRound(round: Round, ends: List<End>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            db.execSQL("UPDATE Round SET `index` = `index` + 1 WHERE `index` >= ${round.index}")
            saveRound(db, round, ends)
        }
    }

    fun loadAugmentedRound(item: Round) = AugmentedRound(item)

    fun saveRound(round: AugmentedRound) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            round.round.save(db)
            for(end in round.ends) {
                end.end.roundId = round.round.id
                EndDAO.saveEnd(end.end, end.images, end.shots)
            }
        }
    }
}
