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

package de.dreier.mytargets.shared.wearable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.utils.marshall
import timber.log.Timber

open class WearableClientBase(protected val context: Context) {

    private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(Wearable.API)
            .build()

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val settings = intent.getParcelableExtra<TimerSettings>(EXTRA_TIMER_SETTINGS)
            sendTimerSettings(settings)
        }
    }

    init {
        googleApiClient.connect()
        val filter = IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_LOCAL)
        LocalBroadcastManager.getInstance(context).registerReceiver(timerReceiver, filter)
    }

    open fun disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(timerReceiver)
        googleApiClient.disconnect()
    }

    protected fun sendMessage(path: String, data: ByteArray) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .setResultCallback { nodes ->
                    for (node in nodes.nodes) {
                        sendToNode(node, path, data)
                    }
                }
    }

    private fun sendToNode(node: Node, path: String, data: ByteArray) {
        Wearable.MessageApi.sendMessage(
                googleApiClient, node.id, path, data)
                .setResultCallback { sendMessageResult ->
                    if (!sendMessageResult.status.isSuccess) {
                        Timber.e("Failed to send message with status code: %d",
                                sendMessageResult.status.statusCode)
                    }
                }
    }

    protected fun sendTimerSettings(settings: TimerSettings) {
        val data = settings.marshall()
        sendMessage(TIMER_SETTINGS, data)
    }

    open fun sendTimerSettingsFromLocal(settings: TimerSettings) {
        val intent = Intent(BROADCAST_TIMER_SETTINGS_FROM_LOCAL)
        intent.putExtra(EXTRA_TIMER_SETTINGS, settings)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendTimerSettingsFromRemote() {
        val intent = Intent(BROADCAST_TIMER_SETTINGS_FROM_REMOTE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    companion object {
        val TRAINING_TEMPLATE = "/de/dreier/mytargets/training/last"
        val TRAINING_CREATE = "/de/dreier/mytargets/training/create"
        val TRAINING_UPDATE = "/de/dreier/mytargets/training/update"
        val END_UPDATE = "/de/dreier/mytargets/end/update"
        val TIMER_SETTINGS = "/de/dreier/mytargets/timer/settings"

        private val BROADCAST_TIMER_SETTINGS_FROM_LOCAL = "timer_settings_local"
        val BROADCAST_TIMER_SETTINGS_FROM_REMOTE = "timer_settings_remote"
        private val EXTRA_TIMER_SETTINGS = "timer_settings"
    }
}
