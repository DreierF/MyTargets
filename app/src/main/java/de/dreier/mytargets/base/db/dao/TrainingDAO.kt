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
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training

@Dao
abstract class TrainingDAO {
    @Query("SELECT * FROM Training")
    abstract fun loadTrainings(): List<Training>

    @Query("SELECT * FROM Training WHERE id = :id")
    abstract fun loadTraining(id: Long): Training

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveTraining(training: Training): Long

    @Update
    abstract fun updateTraining(training: Training)

    @Transaction
    open fun saveTraining(training: Training, rounds: List<Round>) {
        training.id = saveTraining(training)
        for (round in rounds) {
            round.trainingId = training.id
            round.id = insertRound(round)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRound(round: Round): Long

    @Delete
    abstract fun deleteTraining(training: Training)
}
