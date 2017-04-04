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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.base.WearableListenerServiceBase;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.NotificationInfo$$Parcelable;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;

import static de.dreier.mytargets.RoundActivity.EXTRA_TIMER;
import static de.dreier.mytargets.shared.utils.WearableUtils.TRAINING_UPDATE;
import static org.parceler.Parcels.unwrap;

public class WearableListener extends WearableListenerServiceBase {

    public static final String BROADCAST_TRAINING_UPDATED = "training_updated";
    private static final String BROADCAST_UPDATE_END_FROM_LOCAL = "end_from_local";
    private static final String BROADCAST_REQUEST_INFO = "request_info";
    private static final String EXTRA_END = "end";
    public static final String EXTRA_INFO = "info";
    private static final int NOTIFICATION_ID = 1;

    private NotificationInfo info;
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            End end = unwrap(intent.getParcelableExtra(EXTRA_END));
            updateEnd(end);
        }
    };
    private BroadcastReceiver requestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendTrainingUpdate(WearableListener.this, info);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_END_FROM_LOCAL));
        LocalBroadcastManager.getInstance(this).registerReceiver(requestReceiver,
                new IntentFilter(BROADCAST_REQUEST_INFO));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(requestReceiver);
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();
        if (messageEvent.getPath().equals(TRAINING_UPDATE)) {
            info = getNotificationInfo(data);
            showNotification(info);
            sendTrainingUpdate(this, info);
        }
    }

    private void updateEnd(End end) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(end));
        sendMessage(WearableUtils.END_UPDATE, data);
    }

    private static void sendTrainingUpdate(Context context, NotificationInfo info) {
        Intent intent = new Intent(BROADCAST_TRAINING_UPDATED);
        intent.putExtra(EXTRA_INFO, Parcels.wrap(info));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendEndUpdate(Context context, End end) {
        Intent intent = new Intent(BROADCAST_UPDATE_END_FROM_LOCAL);
        intent.putExtra(EXTRA_END, Parcels.wrap(end));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendRequestInfo(Context context) {
        Intent intent = new Intent(BROADCAST_REQUEST_INFO);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
