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

import android.util.Log;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.joda.time.LocalDate;
import org.parceler.Parcels;

import java.util.Collection;
import java.util.HashSet;

import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.End$$Parcelable;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;

/**
 * Listens for incomming connections of wearable devices.
 * On request the class takes care of creating a new training or
 * adding an end to an existing training.
 */
public class WearableListener extends WearableListenerService {

    private static final String TAG = "WearableListener";
    protected GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        switch (messageEvent.getPath()) {
            case WearableUtils.TRAINING_CREATE:
                createTraining();
                break;
            case WearableUtils.END_UPDATE:
                endUpdated(messageEvent);
                break;
        }
    }

    public void createTraining() {
        Optional<Training> training = Stream.of(Training.getAll()).sorted().findFirst();
        if (training.isPresent()) {
            Training t = training.get();
            t.date = LocalDate.now();
            t.arrowNumbering = false;
            for (Round round : t.getRounds()) {
                round.setId(null);
            }
            t.setId(null);
            t.save();
            startTraining(t);
        } else {
            //show opened on phone animation
            sendMessage(WearableUtils.TRAINING_CREATE_ON_PHONE, null);
        }
    }

    private void endUpdated(MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        End end = Parcels.unwrap(ParcelableUtil.unmarshall(data, End$$Parcelable.CREATOR));
        End newEnd = Round.get(end.roundId).addEnd();
        newEnd.exact = false;
        newEnd.setShots(end.getShots());
        //TODO send local broadcast to InputActivity, RoundActivity, TrainingActivity, ScoreboardActivity and StatisticsActivity
    }

    private void startTraining(Training training) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(training));
        sendMessage(WearableUtils.TRAINING_START, data);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

    }

    private void sendMessage(String path, byte[] data) {
        // Send message to all available nodes
        final Collection<String> nodes = getNodes();
        for (String nodeId : nodes) {
            Wearable.MessageApi.sendMessage(
                    googleApiClient, nodeId, path, data)
                    .setResultCallback(
                            sendMessageResult -> {
                                if (!sendMessageResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "Failed to send message with status code: "
                                            + sendMessageResult.getStatus().getStatusCode());
                                }
                            }
                    );
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }
}