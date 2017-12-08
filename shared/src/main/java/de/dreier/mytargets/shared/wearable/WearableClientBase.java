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

package de.dreier.mytargets.shared.wearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import timber.log.Timber;

public class WearableClientBase {

    public static final String TRAINING_TEMPLATE = "/de/dreier/mytargets/training/last";
    public static final String TRAINING_CREATE = "/de/dreier/mytargets/training/create";
    public static final String TRAINING_UPDATE = "/de/dreier/mytargets/training/update";
    public static final String END_UPDATE = "/de/dreier/mytargets/end/update";
    public static final String TIMER_SETTINGS = "/de/dreier/mytargets/timer/settings";

    private static final String BROADCAST_TIMER_SETTINGS_FROM_LOCAL = "timer_settings_local";
    public static final String BROADCAST_TIMER_SETTINGS_FROM_REMOTE = "timer_settings_remote";
    private static final String EXTRA_TIMER_SETTINGS = "timer_settings";

    private GoogleApiClient googleApiClient;
    @NonNull
    protected final Context context;

    @NonNull
    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            TimerSettings settings = Parcels
                    .unwrap(intent.getParcelableExtra(EXTRA_TIMER_SETTINGS));
            sendTimerSettings(settings);
        }
    };

    public WearableClientBase(@NonNull Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
        IntentFilter filter = new IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_LOCAL);
        LocalBroadcastManager.getInstance(context).registerReceiver(timerReceiver, filter);
    }

    public void disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(timerReceiver);
        googleApiClient.disconnect();
    }

    protected void sendMessage(String path, byte[] data) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .setResultCallback(nodes -> {
                    for (Node node : nodes.getNodes()) {
                        sendToNode(node, path, data);
                    }
                });
    }

    private void sendToNode(@NonNull Node node, String path, byte[] data) {
        Wearable.MessageApi.sendMessage(
                googleApiClient, node.getId(), path, data)
                .setResultCallback(
                        sendMessageResult -> {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Timber.e("Failed to send message with status code: %d",
                                        sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                );
    }

    protected void sendTimerSettings(TimerSettings settings) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(settings));
        sendMessage(TIMER_SETTINGS, data);
    }

    public void sendTimerSettingsFromLocal(TimerSettings settings) {
        Intent intent = new Intent(BROADCAST_TIMER_SETTINGS_FROM_LOCAL);
        intent.putExtra(EXTRA_TIMER_SETTINGS, Parcels.wrap(settings));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendTimerSettingsFromRemote() {
        Intent intent = new Intent(BROADCAST_TIMER_SETTINGS_FROM_REMOTE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
