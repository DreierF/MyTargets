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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import timber.log.Timber;

public class WearableClientBase implements GoogleApiClient.ConnectionCallbacks {

    public static final String TRAINING_INFO = "/de/dreier/mytargets/training/info";
    public static final String TRAINING_CREATE = "/de/dreier/mytargets/training/create";
    public static final String TRAINING_CREATE_ON_PHONE = "/de/dreier/mytargets/training/create/phone";
    public static final String TRAINING_UPDATE = "/de/dreier/mytargets/training/update";
    public static final String END_UPDATE = "/de/dreier/mytargets/end/update";
    public static final String TIMER_ENABLE = "/de/dreier/mytargets/timer/enable";
    public static final String TIMER_DISABLE = "/de/dreier/mytargets/timer/disable";


    private GoogleApiClient googleApiClient;
    protected final Context context;

    public WearableClientBase(Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();

        googleApiClient.connect();
        Timber.d("WearableClientBase() called with: context = [" + context + "]");
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected() called with: bundle = [" + bundle + "]");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended() called with: i = [" + i + "]");
    }

    protected void sendMessage(String path, byte[] data) {
        Timber.d("sendMessage() called with: path = [" + path + "]");
        Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .setResultCallback(nodes -> {
                    for (Node node : nodes.getNodes()) {
                        sendToNode(node, path, data);
                    }
                });
    }

    private void sendToNode(Node node, String path, byte[] data) {
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

    public void updateEnd(End end) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(end));
        sendMessage(WearableClientBase.END_UPDATE, data);
    }

    public void createOnPhone() {
        sendMessage(WearableClientBase.TRAINING_CREATE_ON_PHONE, null);
    }
}
