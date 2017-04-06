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

package de.dreier.mytargets;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TimerSettings$$Parcelable;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.TrainingInfo$$Parcelable;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.models.db.Training$$Parcelable;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.utils.WearSettingsManager;

import static de.dreier.mytargets.shared.wearable.WearableClientBase.TIMER_SETTINGS;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.TRAINING_TEMPLATE;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.TRAINING_UPDATE;

public class WearWearableListener extends WearableListenerService {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        byte[] data = messageEvent.getData();
        switch (messageEvent.getPath()) {
            case TRAINING_UPDATE:
                TrainingInfo info = Parcels
                        .unwrap(ParcelableUtil.unmarshall(data, TrainingInfo$$Parcelable.CREATOR));
                showNotification(info);
                ApplicationInstance.wearableClient.sendTrainingUpdate(info);
                break;
            case TRAINING_TEMPLATE:
                Training training = Parcels
                        .unwrap(ParcelableUtil.unmarshall(data, Training$$Parcelable.CREATOR));
                ApplicationInstance.wearableClient.sendTrainingTemplate(training);
                break;
            case TIMER_SETTINGS:
                TimerSettings settings = Parcels
                        .unwrap(ParcelableUtil.unmarshall(data, TimerSettings$$Parcelable.CREATOR));
                WearSettingsManager.setTimerSettings(settings);
                ApplicationInstance.wearableClient.sendTimerSettingsFromRemote();
                break;
            default:
                break;
        }
    }

    private void showNotification(TrainingInfo info) {
        // Build the intent to display our custom notification
        Intent notificationIntent = new Intent(this, RoundActivity.class);
        notificationIntent.putExtra(RoundActivity.EXTRA_ROUND, Parcels.wrap(info.round));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create the ongoing notification
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setContentTitle(info.title)
                        .setContentText(describe(info))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .extend(new Notification.WearableExtender()
                                .setBackground(image));

        // Build the notification and show it
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private String describe(TrainingInfo info) {
        return info.getRoundDetails(this) + "\n" +
                info.getEndDetails(this) + "\n" +
                info.round.distance.toString();
    }
}
