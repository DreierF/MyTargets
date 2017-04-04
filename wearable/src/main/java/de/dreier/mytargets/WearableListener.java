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
import android.support.annotation.Nullable;

import com.google.android.gms.wearable.MessageEvent;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.NotificationInfo$$Parcelable;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.wearable.WearableListenerServiceBase;
import timber.log.Timber;

import static de.dreier.mytargets.RoundActivity.EXTRA_TIMER;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.TRAINING_UPDATE;
import static org.parceler.Parcels.unwrap;

public class WearableListener extends WearableListenerServiceBase {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Timber.d("onMessageReceived: ");

        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();
        if (messageEvent.getPath().equals(TRAINING_UPDATE)) {
            NotificationInfo info = getNotificationInfo(data);
            showNotification(info);
            ApplicationInstance.wearableClient.sendTrainingUpdate(info);
        }
    }

    @Nullable
    private NotificationInfo getNotificationInfo(byte[] data) {
        return unwrap(ParcelableUtil.unmarshall(data, NotificationInfo$$Parcelable.CREATOR));
    }

    private void showNotification(NotificationInfo info) {
        // Build the intent to display our custom notification
        Intent notificationIntent = new Intent(this, RoundActivity.class);
        notificationIntent.putExtra(RoundActivity.EXTRA_ROUND, Parcels.wrap(info.round));
        notificationIntent.putExtra(EXTRA_TIMER, info.timerEnabled);
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

    private String describe(NotificationInfo info) {
        String rounds = getResources().getQuantityString(R.plurals.rounds, info.roundCount, info.roundCount);
        Integer endCount = info.round.maxEndCount == null ? info.round.getEnds().size() : info.round.maxEndCount;
        String ends = getResources().getQuantityString(R.plurals.ends_arrow, info.round.shotsPerEnd, endCount, info.round.shotsPerEnd);
        return rounds + "\n" + ends;
    }
}
