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

package de.dreier.mytargets

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.systemService
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.utils.WearWearableClient



/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
class ApplicationInstance : SharedApplicationInstance() {

    override fun onCreate() {
        super.onCreate()
        wearableClient = WearWearableClient(this)
        initChannels(this)
    }

    private fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val channel = NotificationChannel(DEFAULT_CHANNEL_ID, getString(R.string.notification_channel_default), NotificationManager.IMPORTANCE_DEFAULT)
        context.systemService<NotificationManager>().createNotificationChannel(channel)
    }

    override fun onTerminate() {
        wearableClient.disconnect()
        super.onTerminate()
    }

    companion object {
        lateinit var wearableClient: WearWearableClient
        const val DEFAULT_CHANNEL_ID = "default"
    }
}
