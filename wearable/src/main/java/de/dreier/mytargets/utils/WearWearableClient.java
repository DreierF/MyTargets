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
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;
import de.dreier.mytargets.shared.utils.ParcelableUtilKt;
import de.dreier.mytargets.shared.wearable.WearableClientBase;

public class WearWearableClient extends WearableClientBase {

    public static final String BROADCAST_TRAINING_TEMPLATE = "training_template";
    private static final String BROADCAST_TRAINING_CREATE = "training_create";
    public static final String BROADCAST_TRAINING_UPDATED = "training_updated";
    private static final String BROADCAST_UPDATE_END_FROM_LOCAL = "end_from_local";
    private static final String BROADCAST_REQUEST_TRAINING_TEMPLATE = "request_info";

    public static final String EXTRA_TRAINING = "training";
    private static final String EXTRA_END = "end";
    public static final String EXTRA_INFO = "info";

    @NonNull
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            switch (intent.getAction()) {
                case BROADCAST_REQUEST_TRAINING_TEMPLATE:
                    sendMessage(TRAINING_TEMPLATE, null);
                    break;
                case BROADCAST_TRAINING_CREATE:
                    AugmentedTraining training = intent.getParcelableExtra(EXTRA_TRAINING);
                    createTraining(training);
                    break;
                case BROADCAST_UPDATE_END_FROM_LOCAL:
                    AugmentedEnd end = intent.getParcelableExtra(EXTRA_END);
                    updateEnd(end);
                    break;
                default:
                    break;
            }
        }
    };

    public WearWearableClient(@NonNull Context context) {
        super(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_UPDATE_END_FROM_LOCAL);
        filter.addAction(BROADCAST_REQUEST_TRAINING_TEMPLATE);
        filter.addAction(BROADCAST_TRAINING_CREATE);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.disconnect();
    }

    private void updateEnd(AugmentedEnd end) {
        final byte[] data = ParcelableUtilKt.marshall(end);
        sendMessage(WearableClientBase.END_UPDATE, data);
    }

    private void createTraining(AugmentedTraining training) {
        final byte[] data = ParcelableUtilKt.marshall(training);
        sendMessage(WearableClientBase.TRAINING_CREATE, data);
    }

    public void sendTrainingUpdate(TrainingInfo info) {
        Intent intent = new Intent(BROADCAST_TRAINING_UPDATED);
        intent.putExtra(EXTRA_INFO, info);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendEndUpdate(AugmentedEnd end) {
        Intent intent = new Intent(BROADCAST_UPDATE_END_FROM_LOCAL);
        intent.putExtra(EXTRA_END, end);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void requestNewTrainingTemplate() {
        Intent intent = new Intent(BROADCAST_REQUEST_TRAINING_TEMPLATE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendCreateTraining(AugmentedTraining training) {
        Intent intent = new Intent(BROADCAST_TRAINING_CREATE);
        intent.putExtra(EXTRA_TRAINING, training);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendTrainingTemplate(AugmentedTraining training) {
        Intent intent = new Intent(BROADCAST_TRAINING_TEMPLATE);
        intent.putExtra(EXTRA_TRAINING, training);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void sendTimerSettingsFromLocal(TimerSettings settings) {
        super.sendTimerSettingsFromLocal(settings);
        WearSettingsManager.setTimerSettings(settings);
    }
}
