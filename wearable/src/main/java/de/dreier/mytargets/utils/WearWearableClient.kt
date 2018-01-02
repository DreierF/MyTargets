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
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.models.TrainingInfo
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.utils.marshall
import de.dreier.mytargets.shared.wearable.WearableClientBase

class WearWearableClient(context: Context) : WearableClientBase(context) {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_REQUEST_TRAINING_TEMPLATE -> sendMessage(WearableClientBase.Companion.REQUEST_TRAINING_TEMPLATE, ByteArray(0))
                BROADCAST_TRAINING_CREATE -> {
                    val training = intent.getParcelableExtra<AugmentedTraining>(EXTRA_TRAINING)
                    createTraining(training)
                }
                BROADCAST_UPDATE_END_FROM_LOCAL -> {
                    val end = intent.getParcelableExtra<AugmentedEnd>(EXTRA_END)
                    updateEnd(end)
                }
                else -> {
                }
            }
        }
    }

    init {
        val filter = IntentFilter()
        filter.addAction(BROADCAST_UPDATE_END_FROM_LOCAL)
        filter.addAction(BROADCAST_REQUEST_TRAINING_TEMPLATE)
        filter.addAction(BROADCAST_TRAINING_CREATE)
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter)
    }

    override fun disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
        super.disconnect()
    }

    private fun updateEnd(end: AugmentedEnd) {
        val data = end.marshall()
        sendMessage(WearableClientBase.END_UPDATE, data)
    }

    private fun createTraining(training: AugmentedTraining) {
        val data = training.marshall()
        sendMessage(WearableClientBase.TRAINING_CREATE, data)
    }

    fun sendTrainingUpdate(info: TrainingInfo) {
        val intent = Intent(BROADCAST_TRAINING_UPDATED)
        intent.putExtra(EXTRA_INFO, info)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendEndUpdate(end: AugmentedEnd) {
        val intent = Intent(BROADCAST_UPDATE_END_FROM_LOCAL)
        intent.putExtra(EXTRA_END, end)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun requestNewTrainingTemplate() {
        val intent = Intent(BROADCAST_REQUEST_TRAINING_TEMPLATE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendCreateTraining(training: AugmentedTraining) {
        val intent = Intent(BROADCAST_TRAINING_CREATE)
        intent.putExtra(EXTRA_TRAINING, training)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendTrainingTemplate(training: AugmentedTraining) {
        val intent = Intent(BROADCAST_TRAINING_TEMPLATE)
        intent.putExtra(EXTRA_TRAINING, training)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun sendTimerSettingsFromLocal(settings: TimerSettings) {
        super.sendTimerSettingsFromLocal(settings)
        WearSettingsManager.timerSettings = settings
    }

    companion object {
        const val BROADCAST_TRAINING_TEMPLATE = "training_template"
        private const val BROADCAST_TRAINING_CREATE = "training_create"
        const val BROADCAST_TRAINING_UPDATED = "training_updated"
        private const val BROADCAST_UPDATE_END_FROM_LOCAL = "end_from_local"
        private const val BROADCAST_REQUEST_TRAINING_TEMPLATE = "request_info"
        const val EXTRA_TRAINING = "training"
        private const val EXTRA_END = "end"
        const val EXTRA_INFO = "info"
    }
}
