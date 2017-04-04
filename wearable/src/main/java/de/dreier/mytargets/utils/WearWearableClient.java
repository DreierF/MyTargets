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

package de.dreier.mytargets.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.wearable.WearableClientBase;
import timber.log.Timber;

import static org.parceler.Parcels.unwrap;

public class WearWearableClient extends WearableClientBase {

    public static final String BROADCAST_TRAINING_UPDATED = "training_updated";
    private static final String BROADCAST_UPDATE_END_FROM_LOCAL = "end_from_local";
    private static final String BROADCAST_REQUEST_INFO = "request_info";

    private static final String EXTRA_END = "end";
    public static final String EXTRA_INFO = "info";

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
            if (info == null) {
                return;
            }
            sendTrainingUpdate(info);
        }
    };

    public WearWearableClient(Context context) {
        super(context);

        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_END_FROM_LOCAL));
        LocalBroadcastManager.getInstance(context).registerReceiver(requestReceiver,
                new IntentFilter(BROADCAST_REQUEST_INFO));
    }

    @Override
    public void disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(requestReceiver);
        super.disconnect();
    }

    public void sendTrainingUpdate(NotificationInfo info) {
        this.info = info;
        Timber.d("sendTrainingUpdate: send broadcast");
        Intent intent = new Intent(BROADCAST_TRAINING_UPDATED);
        intent.putExtra(EXTRA_INFO, Parcels.wrap(info));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendEndUpdate(End end) {
        Intent intent = new Intent(BROADCAST_UPDATE_END_FROM_LOCAL);
        intent.putExtra(EXTRA_END, Parcels.wrap(end));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendRequestInfo() {
        Intent intent = new Intent(BROADCAST_REQUEST_INFO);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
