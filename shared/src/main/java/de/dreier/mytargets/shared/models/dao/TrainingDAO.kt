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
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.*

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

    fun loadAugmentedTraining(id: Long): AugmentedTraining = AugmentedTraining(loadTraining(id), loadRounds(id)
            .map { RoundDAO.loadAugmentedRound(it) }
            .toMutableList())

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
            saveTraining(db, training, rounds)
        }
    }

    private fun saveTraining(db: DatabaseWrapper, training: Training, rounds: List<Round>) {
        training.save(db)
//        SQLite.delete(Round::class.java)
//                .where(Round_Table.training.eq(training.id))
//                .execute(db)
        for (round in rounds) {
            round.trainingId = training.id
            round.save(db)
        }
    }

    fun insertTraining(training: AugmentedTraining) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            training.training.save(db)
            training.rounds.forEach { round ->
                round.round.trainingId = training.training.id
                round.round.save(db)
                for (end in round.ends) {
                    end.end.roundId = round.round.id
                    end.end.save(db)
                }
            }
        }
    }

    fun deleteTraining(training: Training) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            training.delete(db)
        }
    }

    fun getOrCreateArcherSignature(training: Training): Signature {
        if (training.archerSignatureId != null) {
            val signature = SignatureDAO.loadSignatureOrNull(training.archerSignatureId!!)
            if (signature != null) {
                return signature
            }
        }
        val signature = Signature()
        SignatureDAO.saveSignature(signature)
        training.archerSignatureId = signature.id
        TrainingDAO.saveTraining(training)
        return signature
    }

    fun getOrCreateWitnessSignature(training: Training): Signature {
        if (training.witnessSignatureId != null) {
            val signature = SignatureDAO.loadSignatureOrNull(training.witnessSignatureId!!)
            if (signature != null) {
                return signature
            }
        }
        val signature = Signature()
        SignatureDAO.saveSignature(signature)
        training.witnessSignatureId = signature.id
        TrainingDAO.saveTraining(training)
        return signature
    }
}
