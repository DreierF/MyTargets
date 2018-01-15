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
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Round_Table
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.models.db.Training_Table

object TrainingDAO {
    fun loadTrainings(): List<Training> = SQLite.select()
            .from(Training::class.java)
            .queryList()

    fun loadTrainings(trainingIds: LongArray): List<Training> = SQLite.select()
            .from(Training::class.java)
            .where(Training_Table._id.`in`(trainingIds.toList()))
            .queryList()

    fun loadTraining(id: Long): Training = SQLite.select()
            .from(Training::class.java)
            .where(Training_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("Training $id does not exist")

    fun loadTrainingOrNull(id: Long): Training? = SQLite.select()
            .from(Training::class.java)
            .where(Training_Table._id.eq(id))
            .querySingle()

    fun loadRounds(id: Long): MutableList<Round> = SQLite.select()
            .from(Round::class.java)
            .where(Round_Table.training.eq(id))
            .orderBy(Round_Table.index, true)
            .queryList().toMutableList()

    fun saveTraining(training: Training) {
        training.save()
    }

    fun saveTraining(training: Training, rounds: List<Round>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            saveTraining(training, db, rounds)
        }
    }

    fun insertTraining(training: Training, rounds: List<Round>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            saveTraining(training, db, rounds)
        }
    }

    private fun saveTraining(training: Training, db: DatabaseWrapper, rounds: List<Round>) {
        training.save(db)
//        SQLite.delete(Round::class.java)
//                .where(Round_Table.training.eq(training.id))
//                .execute(db)
        for (round in rounds) {
            round.trainingId = training.id
            round.save(db)
        }
    }

    fun deleteTraining(training: Training) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            training.delete(db)
        }
    }

    fun insertTraining(training: AugmentedTraining) {
        TrainingDAO.insertTraining(training.training, training.rounds.map { it.round })
        training.rounds.forEach {
            RoundDAO.saveRound(it)
        }
    }

    fun loadAugmentedTraining(item: Training) = AugmentedTraining(item)
}
