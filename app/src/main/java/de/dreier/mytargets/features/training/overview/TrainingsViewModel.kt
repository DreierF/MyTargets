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

package de.dreier.mytargets.features.training.overview

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.base.db.TrainingRepository
import de.dreier.mytargets.shared.models.db.Training


class TrainingsViewModel(app: ApplicationInstance) : AndroidViewModel(app) {

    val trainings: LiveData<List<Training>>

    private val trainingDAO = ApplicationInstance.db.trainingDAO()
    private val roundDAO = ApplicationInstance.db.roundDAO()
    private val roundRepository = RoundRepository(ApplicationInstance.db)

    private val database = ApplicationInstance.db
    private val trainingRepository = TrainingRepository(
        ApplicationInstance.db,
        trainingDAO,
        roundDAO,
        roundRepository,
        database.signatureDAO()
    )

    init {
        trainings = trainingDAO.loadTrainingsLive()
    }

    fun deleteTraining(item: Training): () -> Training {
        val training = trainingRepository.loadAugmentedTraining(item.id)
        trainingDAO.deleteTraining(item)
        return {
            trainingRepository.insertTraining(training)
            item
        }
    }

    fun getRoundIds(ids: List<Long>) = ids
        .map { trainingDAO.loadTraining(it) }
        .flatMap { t -> roundDAO.loadRounds(t.id) }
        .map { it.id }

    fun getAllRoundIds() = trainingDAO.loadTrainings()
        .flatMap { training -> roundDAO.loadRounds(training.id) }
        .map { it.id }

}
