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

package de.dreier.mytargets.features.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.BROADCAST_TIMER_SETTINGS_FROM_REMOTE

class TimerSettingsFragment : SettingsFragmentBase() {

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            activity!!.recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            timerReceiver,
            IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_REMOTE)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(timerReceiver)
        super.onDestroy()
    }

    override fun updateItemSummaries() {
        val settings = SettingsManager.timerSettings
        setSecondsSummary(SettingsManager.KEY_TIMER_WAIT_TIME, settings.waitTime)
        setSecondsSummary(SettingsManager.KEY_TIMER_SHOOT_TIME, settings.shootTime)
        setSecondsSummary(SettingsManager.KEY_TIMER_WARN_TIME, settings.warnTime)
        ApplicationInstance.wearableClient.sendTimerSettingsFromLocal(settings)
    }

    private fun setSecondsSummary(key: String, value: Int) {
        setSummary(key, resources.getQuantityString(R.plurals.second, value, value))
    }
}
