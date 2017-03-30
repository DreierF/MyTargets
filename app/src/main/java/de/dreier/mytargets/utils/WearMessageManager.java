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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import java.util.Collection;
import java.util.HashSet;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.End$$Parcelable;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class WearMessageManager
        implements GoogleApiClient.OnConnectionFailedListener,MessageApi.MessageListener {

    private static final String TAG = "wearMessageManager";
    private final TargetViewBase.OnEndFinishedListener mListener;

    private final GoogleApiClient mGoogleApiClient;

    public WearMessageManager(Context context, NotificationInfo info) {
        if (!(context instanceof TargetViewBase.OnEndFinishedListener)) {
            throw new ClassCastException();
        }
        mListener = (TargetViewBase.OnEndFinishedListener) context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "Connected to Google Api Service");
                        }
                        Wearable.MessageApi.addListener(mGoogleApiClient, WearMessageManager.this);
                        sendMessage(info, WearableUtils.STARTED_ROUND);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendMessage(NotificationInfo info, String path) {
        // Serialize bundle to byte array
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(info));
        new Thread(() -> {
            sendMessage(path, data);
        }).start();
    }

    public void sendMessageUpdate(NotificationInfo info) {
        sendMessage(info, WearableUtils.UPDATE_ROUND);
    }

    private void sendMessage(String path, byte[] data) {
        // Send message to all available nodes
        final Collection<String> nodes = getNodes();
        for (String nodeId : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, nodeId, path, data).setResultCallback(
                    sendMessageResult -> {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
            );
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();
        End end = Parcels.unwrap(ParcelableUtil.unmarshall(data, End$$Parcelable.CREATOR));

        if (messageEvent.getPath().equals(WearableUtils.FINISHED_INPUT)) {
            mListener.onEndFinished(end.getShots(), true);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void close() {
        if (mGoogleApiClient.isConnected()) {
            sendMessageStopped();
        }
    }

    private void sendMessageStopped() {
        new Thread(() -> {
            sendMessage(WearableUtils.STOPPED_ROUND, new byte[0]);
            Wearable.MessageApi.removeListener(mGoogleApiClient, WearMessageManager.this);
            mGoogleApiClient.disconnect();
        }).start();
    }
}
