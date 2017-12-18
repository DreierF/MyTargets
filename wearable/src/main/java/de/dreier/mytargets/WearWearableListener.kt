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

package de.dreier.mytargets

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.models.TrainingInfo
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.utils.unmarshall
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.REQUEST_TRAINING_TEMPLATE
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.TIMER_SETTINGS
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.TRAINING_UPDATE
import de.dreier.mytargets.utils.WearSettingsManager

class WearWearableListener : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val data = messageEvent.data
        when (messageEvent.path) {
            TRAINING_UPDATE -> {
                val info = data.unmarshall(TrainingInfo.CREATOR as Parcelable.Creator<TrainingInfo>)
                showNotification(info)
                ApplicationInstance.wearableClient.sendTrainingUpdate(info)
            }
            REQUEST_TRAINING_TEMPLATE -> {
                val training = data.unmarshall(AugmentedTraining.CREATOR as Parcelable.Creator<AugmentedTraining>)
                ApplicationInstance.wearableClient.sendTrainingTemplate(training)
            }
            TIMER_SETTINGS -> {
                val settings = data.unmarshall(TimerSettings.CREATOR as Parcelable.Creator<TimerSettings>)
                WearSettingsManager.timerSettings = settings
                ApplicationInstance.wearableClient.sendTimerSettingsFromRemote()
            }
            else -> {
            }
        }
    }

    private fun showNotification(info: TrainingInfo) {
        // Build the intent to display our custom notification
        val notificationIntent = Intent(this, RoundActivity::class.java)
        notificationIntent.putExtra(RoundActivity.EXTRA_ROUND, info.round)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Create the ongoing notification
        val image = BitmapFactory.decodeResource(resources, R.drawable.wear_bg)
        val notificationBuilder = NotificationCompat.Builder(this, ApplicationInstance.DEFAULT_CHANNEL_ID)
                .setContentTitle(info.title)
                .setContentText(describe(info))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .extend(NotificationCompat.WearableExtender().setBackground(image))

        // Build the notification and show it
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun describe(info: TrainingInfo): String {
        return info.getRoundDetails(this) + "\n" +
                info.getEndDetails(this) + "\n" +
                info.round.round.distance.toString()
    }

    companion object {
        private val NOTIFICATION_ID = 1
    }
}
