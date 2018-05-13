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

package de.dreier.mytargets.utils

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.EndRepository
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.base.db.TrainingRepository
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Environment
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.utils.unmarshall
import de.dreier.mytargets.shared.wearable.WearableClientBase
import org.threeten.bp.LocalDate

/**
 * Listens for incoming connections of wearable devices.
 * On request the class takes care of creating a new training or
 * adding an end to an existing training.
 */
class MobileWearableListener : WearableListenerService() {

    private val database = ApplicationInstance.db
    private val trainingDAO = database.trainingDAO()
    private val roundDAO = database.roundDAO()
    private val endDAO = database.endDAO()
    private val standardRoundDAO = database.standardRoundDAO()
    private val roundRepository = RoundRepository(database)
    private val trainingRepository = TrainingRepository(database, trainingDAO, roundDAO,roundRepository, database.signatureDAO())

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            WearableClientBase.TRAINING_CREATE -> createTraining(messageEvent)
            WearableClientBase.END_UPDATE -> endUpdated(messageEvent)
            WearableClientBase.REQUEST_TRAINING_TEMPLATE -> trainingTemplate()
            WearableClientBase.TIMER_SETTINGS -> timerSettings(messageEvent)
            else -> {
            }
        }
    }

    private fun timerSettings(messageEvent: MessageEvent) {
        val settings = messageEvent.data.unmarshall(TimerSettings.CREATOR)
        SettingsManager.timerSettings = settings
        ApplicationInstance.wearableClient.sendTimerSettingsFromRemote()
    }

    private fun trainingTemplate() {
        val lastTraining = trainingDAO.loadTrainings().minWith(compareByDescending(Training::date).thenByDescending(Training::id))
        if (lastTraining != null && lastTraining.date.isEqual(LocalDate.now())) {
            val training = trainingRepository.loadAugmentedTraining(lastTraining.id)
            ApplicationInstance.wearableClient.updateTraining(training)
        } else {
            val training = Training()
            training.title = getString(R.string.training)
            training.date = LocalDate.now()
            training.environment = Environment.getDefault(SettingsManager.indoor)
            training.bowId = SettingsManager.bow
            training.arrowId = SettingsManager.arrow
            training.arrowNumbering = false
            val aTraining = AugmentedTraining(training, mutableListOf())

            val freeTraining = lastTraining?.standardRoundId == null
            if (freeTraining) {
                val round = Round()
                round.target = SettingsManager.target
                round.shotsPerEnd = SettingsManager.shotsPerEnd
                round.maxEndCount = null
                round.distance = SettingsManager.distance
                aTraining.rounds = mutableListOf(AugmentedRound(round, mutableListOf()))
            } else {
                aTraining.rounds = standardRoundDAO
                        .loadAugmentedStandardRound(lastTraining!!.standardRoundId!!)
                        .createRoundsFromTemplate()
                        .map { AugmentedRound(it, mutableListOf()) }
                        .toMutableList()
            }
            ApplicationInstance.wearableClient.sendTrainingTemplate(aTraining)
        }
    }

    private fun createTraining(messageEvent: MessageEvent) {
        val augmentedTraining = messageEvent.data.unmarshall(AugmentedTraining.CREATOR)
        trainingDAO.saveTraining(augmentedTraining.training, augmentedTraining.rounds.map { it.round })
        ApplicationInstance.wearableClient.updateTraining(augmentedTraining)
        ApplicationInstance.wearableClient.sendCreateTrainingFromRemoteBroadcast()
    }

    private fun endUpdated(messageEvent: MessageEvent) {
        val (end, shots) = messageEvent.data.unmarshall(AugmentedEnd.CREATOR)
        val round = roundRepository.loadAugmentedRound(end.roundId!!)
        val newEnd = getLastEmptyOrCreateNewEnd(round)
        newEnd.end.exact = false
        newEnd.shots = shots
        endDAO.saveCompleteEnd(newEnd.end, newEnd.images, newEnd.shots)

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round.round, newEnd.end)
        ApplicationInstance.wearableClient
                .sendUpdateTrainingFromLocalBroadcast(trainingRepository.loadAugmentedTraining(round.round.trainingId!!))
    }

    private fun getLastEmptyOrCreateNewEnd(round: AugmentedRound): AugmentedEnd {
        if (round.ends.isEmpty()) {
            return round.addEnd()
        }
        val lastEnd = round.ends[round.ends.size - 1]
        return if (lastEnd.isEmpty) {
            lastEnd
        } else {
            round.addEnd()
        }
    }
}
