/*
 * Copyright (C) 2017 Florian Dreier
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.TrainingInfo
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.streamwrapper.Stream
import de.dreier.mytargets.shared.utils.marshall
import de.dreier.mytargets.shared.wearable.WearableClientBase
import timber.log.Timber

class MobileWearableClient(context: Context) : WearableClientBase(context) {

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val training = intent.getParcelableExtra<AugmentedTraining>(EXTRA_TRAINING)
            updateTraining(training)
        }
    }

    init {
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver,
                IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_LOCAL))
    }

    override fun disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver)
        super.disconnect()
    }

    fun sendUpdateTrainingFromLocalBroadcast(training: AugmentedTraining) {
        val intent = Intent(BROADCAST_UPDATE_TRAINING_FROM_LOCAL)
        intent.putExtra(EXTRA_TRAINING, training)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendUpdateTrainingFromRemoteBroadcast(round: Round, end: End) {
        val intent = Intent(BROADCAST_UPDATE_TRAINING_FROM_REMOTE)
        intent.putExtra(EXTRA_TRAINING_ID, round.trainingId)
        intent.putExtra(EXTRA_ROUND_ID, round.id)
        intent.putExtra(EXTRA_END, end)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendCreateTrainingFromRemoteBroadcast() {
        val intent = Intent(BROADCAST_CREATE_TRAINING_FROM_REMOTE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun updateTraining(training: AugmentedTraining) {
        val rounds = training.rounds
        val roundCount = rounds.size
        if (roundCount < 1) {
            return
        }
        var round = AugmentedRound(rounds[roundCount - 1].toRound())
        for (r in rounds) {
            if (r.round.maxEndCount != null && r.ends.size < r.round.maxEndCount!!) {
                round = r
                break
            }
        }
        val target = round.round.target
        round.ends = round.ends
                .filter { (_, shots) ->
                    shots.all { (_, _, _, _, _, scoringRing) -> scoringRing != Shot.NOTHING_SELECTED }
                }
                .map { end ->
                    if (!SettingsManager.shouldSortTarget(target)) {
                        end
                    } else {
                        AugmentedEnd(end.end, Stream.of(end.shots).sorted().toList(), end.images)
                    }
                }
                .toMutableList()
        val trainingInfo = TrainingInfo(training, round)
        sendTrainingInfo(trainingInfo)
        sendTimerSettings(SettingsManager.timerSettings)
    }

    private fun sendTrainingInfo(trainingInfo: TrainingInfo) {
        val data = trainingInfo.marshall()
        sendMessage(WearableClientBase.TRAINING_UPDATE, data)
    }

    fun sendTrainingTemplate(training: AugmentedTraining) {
        val data = training.marshall()
        sendMessage(WearableClientBase.REQUEST_TRAINING_TEMPLATE, data)
    }

    abstract class EndUpdateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val trainingId = intent.getLongExtra(EXTRA_TRAINING_ID, -1)
            val roundId = intent.getLongExtra(EXTRA_ROUND_ID, -1)
            val end = intent.getParcelableExtra<End>(EXTRA_END)
            if (trainingId == -1L || roundId == -1L) {
                Timber.w("Incomplete request!")
            }
            onUpdate(trainingId, roundId, end)
        }

        protected abstract fun onUpdate(trainingId: Long?, roundId: Long?, end: End)
    }

    companion object {
        private const val BROADCAST_UPDATE_TRAINING_FROM_LOCAL = "update_from_local"
        const val BROADCAST_UPDATE_TRAINING_FROM_REMOTE = "update_from_remote"
        const val BROADCAST_CREATE_TRAINING_FROM_REMOTE = "create_from_remote"
        private const val EXTRA_TRAINING = "training"
        private const val EXTRA_TRAINING_ID = "training_id"
        private const val EXTRA_ROUND_ID = "round_id"
        private const val EXTRA_END = "end_index"
    }
}
