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

package de.dreier.mytargets.features.training.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.LiveDataUtil2


class TrainingViewModel(app: Application) : AndroidViewModel(app) {
    val trainingId = MutableLiveData<Long?>()

    val training: LiveData<Training>
    val rounds: LiveData<List<Round>>
    val trainingAndRounds: LiveData<Pair<Training, List<Round>>>

    private val trainingDAO = ApplicationInstance.db.trainingDAO()
    private val roundDAO = ApplicationInstance.db.roundDAO()
    private val roundRepository = RoundRepository(ApplicationInstance.db)

    init {
        training = Transformations.switchMap(trainingId) { id ->
            if (id == null) {
                null
            } else {
                trainingDAO.loadTrainingLive(id)
            }
        }
        rounds = Transformations.switchMap(trainingId) { id ->
            if (id == null) {
                null
            } else {
                roundDAO.loadRoundsLive(id)
            }
        }
        trainingAndRounds = LiveDataUtil2<Training, List<Round>, Pair<Training, List<Round>>>().map(
            training,
            rounds
        ) { training, rounds ->
            Pair(training, rounds)
        }
    }

    fun setTrainingId(trainingId: Long?) {
        this.trainingId.value = trainingId
    }

    fun setTrainingComment(comment: String) {
        trainingDAO.updateComment(trainingId.value!!, comment)
    }

    fun deleteRound(item: Round): () -> Round {
        val round = roundRepository.loadAugmentedRound(item)
        roundDAO.deleteRound(item)
        return {
            roundRepository.insertRound(round)
            item
        }
    }
}
