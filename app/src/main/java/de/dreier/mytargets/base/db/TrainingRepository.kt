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
import de.dreier.mytargets.base.db.dao.RoundDAO
import de.dreier.mytargets.base.db.dao.SignatureDAO
import de.dreier.mytargets.base.db.dao.TrainingDAO
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.Signature
import de.dreier.mytargets.shared.models.db.Training

class TrainingRepository(
        private val database: RoomDatabase,
        private val trainingDAO: TrainingDAO,
        private val roundDAO: RoundDAO,
        private val roundRepository: RoundRepository,
        private val signatureDAO: SignatureDAO
) {

    fun loadAugmentedTraining(id: Long): AugmentedTraining = AugmentedTraining(trainingDAO.loadTraining(id), roundDAO.loadRounds(id)
            .map { roundRepository.loadAugmentedRound(it) }
            .toMutableList())

    fun insertTraining(training: AugmentedTraining) {
        database.runInTransaction {
            training.training.id = trainingDAO.insertTraining(training.training)
            training.rounds.forEach { round ->
                round.round.trainingId = training.training.id
                roundRepository.saveRound(round)
            }
        }
    }

    fun getOrCreateArcherSignature(training: Training): Signature {
        if (training.archerSignatureId != null) {
            val signature = signatureDAO.loadSignatureOrNull(training.archerSignatureId!!)
            if (signature != null) {
                return signature
            }
        }
        val signature = Signature()
        signature.id = signatureDAO.insertSignature(signature)
        training.archerSignatureId = signature.id
        trainingDAO.updateTraining(training)
        return signature
    }

    fun getOrCreateWitnessSignature(training: Training): Signature {
        if (training.witnessSignatureId != null) {
            val signature = signatureDAO.loadSignatureOrNull(training.witnessSignatureId!!)
            if (signature != null) {
                return signature
            }
        }
        val signature = Signature()
        signature.id = signatureDAO.insertSignature(signature)
        training.witnessSignatureId = signature.id
        trainingDAO.updateTraining(training)
        return signature
    }
}
