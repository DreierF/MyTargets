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
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.utils.marshall

open class WearableClientBase(protected val context: Context) {

    private val nodeClient = Wearable.getNodeClient(context)
    private val msgClient = Wearable.getMessageClient(context)

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val settings = intent.getParcelableExtra<TimerSettings>(EXTRA_TIMER_SETTINGS)
            sendTimerSettings(settings)
        }
    }

    init {
        val filter = IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_LOCAL)
        LocalBroadcastManager.getInstance(context).registerReceiver(timerReceiver, filter)
    }

    open fun disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(timerReceiver)
    }

    protected fun sendMessage(path: String, data: ByteArray) {
        nodeClient.connectedNodes
                .addOnSuccessListener { nodes ->
                    for (node in nodes) {
                        sendToNode(node, path, data)
                    }
                }
    }

    private fun sendToNode(node: Node, path: String, data: ByteArray) {
        msgClient.sendMessage(node.id, path, data)
    }

    protected fun sendTimerSettings(settings: TimerSettings) {
        sendMessage(TIMER_SETTINGS, settings.marshall())
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
        const val REQUEST_TRAINING_TEMPLATE = "/de/dreier/mytargets/training/last"
        const val TRAINING_CREATE = "/de/dreier/mytargets/training/create"
        const val TRAINING_UPDATE = "/de/dreier/mytargets/training/update"
        const val END_UPDATE = "/de/dreier/mytargets/end/update"
        const val TIMER_SETTINGS = "/de/dreier/mytargets/timer/settings"
        private const val BROADCAST_TIMER_SETTINGS_FROM_LOCAL = "timer_settings_local"
        const val BROADCAST_TIMER_SETTINGS_FROM_REMOTE = "timer_settings_remote"
        private const val EXTRA_TIMER_SETTINGS = "timer_settings"
    }
}
