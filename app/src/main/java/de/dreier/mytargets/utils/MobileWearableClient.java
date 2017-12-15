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

import java.util.List;

import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedRound;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.wearable.WearableClientBase;
import timber.log.Timber;

public class MobileWearableClient extends WearableClientBase {

    private static final String BROADCAST_UPDATE_TRAINING_FROM_LOCAL = "update_from_local";
    public static final String BROADCAST_UPDATE_TRAINING_FROM_REMOTE = "update_from_remote";
    public static final String BROADCAST_CREATE_TRAINING_FROM_REMOTE = "create_from_remote";
    private static final String EXTRA_TRAINING = "training";
    private static final String EXTRA_TRAINING_ID = "training_id";
    private static final String EXTRA_ROUND_ID = "round_id";
    private static final String EXTRA_END = "end_index";

    public MobileWearableClient(@NonNull Context context) {
        super(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_LOCAL));
    }

    @Override
    public void disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver);
        super.disconnect();
    }

    @NonNull
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            AugmentedTraining training = intent.getParcelableExtra(EXTRA_TRAINING);
            updateTraining(training);
        }
    };

    public void sendUpdateTrainingFromLocalBroadcast(AugmentedTraining training) {
        Intent intent = new Intent(BROADCAST_UPDATE_TRAINING_FROM_LOCAL);
        intent.putExtra(EXTRA_TRAINING, training);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendUpdateTrainingFromRemoteBroadcast(@NonNull Round round, End end) {
        Intent intent = new Intent(BROADCAST_UPDATE_TRAINING_FROM_REMOTE);
        intent.putExtra(EXTRA_TRAINING_ID, round.getTrainingId());
        intent.putExtra(EXTRA_ROUND_ID, round.getId());
        intent.putExtra(EXTRA_END, end);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendCreateTrainingFromRemoteBroadcast() {
        Intent intent = new Intent(BROADCAST_CREATE_TRAINING_FROM_REMOTE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void updateTraining(@NonNull AugmentedTraining training) {
        List<AugmentedRound> rounds = training.getRounds();
        int roundCount = rounds.size();
        if (roundCount < 1) {
            return;
        }
        AugmentedRound round = rounds.get(roundCount - 1);
        for (AugmentedRound r : rounds) {
            if (r.getRound().getMaxEndCount() != null && r.getEnds().size() < r.getRound().getMaxEndCount()) {
                round = r;
                break;
            }
        }
        Target target = round.getRound().getTarget();
        round.setEnds(Stream.of(round.getEnds())
                .filter(end -> Stream.of(end.getShots())
                        .allMatch(s -> s.getScoringRing() != Shot.Companion.getNOTHING_SELECTED()))
                .map(end -> {
                    if(!SettingsManager.INSTANCE.shouldSortTarget(target)) {
                        return end;
                    }
                    return new AugmentedEnd(end.getEnd(), Stream.of(end.getShots()).sorted().toList());
                })
                .toList());
        TrainingInfo trainingInfo = new TrainingInfo(training, round);
        sendTrainingInfo(trainingInfo);
        sendTimerSettings(SettingsManager.INSTANCE.getTimerSettings());
    }

    private void sendTrainingInfo(TrainingInfo trainingInfo) {
        final byte[] data = ParcelableUtil.marshall(trainingInfo);
        sendMessage(WearableClientBase.TRAINING_UPDATE, data);
    }

    public void sendTrainingTemplate(AugmentedTraining training) {
        final byte[] data = ParcelableUtil.marshall(training);
        sendMessage(WearableClientBase.TRAINING_TEMPLATE, data);
    }

    public abstract static class EndUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Long trainingId = intent.getLongExtra(EXTRA_TRAINING_ID, -1);
            Long roundId = intent.getLongExtra(EXTRA_ROUND_ID, -1);
            End end = intent.getParcelableExtra(EXTRA_END);
            if (trainingId == -1 || roundId == -1) {
                Timber.w("Incomplete request!");
            }
            onUpdate(trainingId, roundId, end);
        }

        protected abstract void onUpdate(Long trainingId, Long roundId, End end);
    }
}
