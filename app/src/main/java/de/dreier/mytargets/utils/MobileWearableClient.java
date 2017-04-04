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
import android.util.Log;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.wearable.MessageEvent;

import org.joda.time.LocalDate;
import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.End$$Parcelable;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.wearable.WearableClientBase;
import timber.log.Timber;

import static org.parceler.Parcels.unwrap;

public class MobileWearableClient extends WearableClientBase {
    public MobileWearableClient(Context context) {
        super(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_LOCAL));
    }

    @Override
    public void disconnect() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver);
        super.disconnect();
    }

    private static final String TAG = "WearableListener";
    private static final String BROADCAST_UPDATE_TRAINING_FROM_LOCAL = "update_from_local";
    public static final String BROADCAST_UPDATE_TRAINING_FROM_REMOTE = "update_from_remote";
    private static final String EXTRA_TRAINING = "training";
    private static final String EXTRA_TRAINING_ID = "training_id";
    private static final String EXTRA_ROUND_ID = "round_id";
    private static final String EXTRA_END = "end_index";

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("updateReceiver received %s", intent.getAction());
            Training training = unwrap(intent.getParcelableExtra(EXTRA_TRAINING));
            updateTraining(training);
        }
    };

    //TODO send local broadcast to InputActivity, RoundActivity, TrainingActivity, ScoreboardActivity and StatisticsActivity
    public void sendUpdateTrainingFromLocalBroadcast(Training training) {
        Intent intent = new Intent(BROADCAST_UPDATE_TRAINING_FROM_LOCAL);
        intent.putExtra(EXTRA_TRAINING, Parcels.wrap(training));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendUpdateTrainingFromRemoteBroadcast(Round round, End end) {
        Intent intent = new Intent(BROADCAST_UPDATE_TRAINING_FROM_REMOTE);
        intent.putExtra(EXTRA_TRAINING_ID, round.trainingId);
        intent.putExtra(EXTRA_ROUND_ID, round.getId());
        intent.putExtra(EXTRA_END, Parcels.wrap(end));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
            updateTraining(t);
        } else {
            //show opened on phone animation
            ApplicationInstance.wearableClient.createOnPhone();
        }
    }

    private void endUpdated(MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        End end = unwrap(ParcelableUtil.unmarshall(data, End$$Parcelable.CREATOR));
        Round round = Round.get(end.roundId);
        End newEnd = round.addEnd();  //TODO change to take last empty end when merging with #4
        newEnd.exact = false;
        newEnd.setShots(end.getShots());
        newEnd.save();

        sendUpdateTrainingFromRemoteBroadcast(round, newEnd);
    }

    public void updateTraining(Training training) {
        Timber.d("updateTraining() called with: training = [" + training + "]");
        List<Round> rounds = training.getRounds();
        int roundCount = rounds.size();
        if (roundCount < 1) {
            return;
        }
        Round round = rounds.get(roundCount - 1);
        NotificationInfo notificationInfo = new NotificationInfo(round, training.title, roundCount,
                SettingsManager.getTimerEnabled());
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(notificationInfo));

        sendMessage(WearableClientBase.TRAINING_UPDATE, data);
    }

    public abstract static class EndUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Long trainingId = intent.getLongExtra(EXTRA_TRAINING_ID, -1);
            Long roundId = intent.getLongExtra(EXTRA_ROUND_ID, -1);
            End end = intent.getParcelableExtra(EXTRA_END);
            if (trainingId == -1 || roundId == -1) {
                Log.w(TAG, "Incomplete request!");
            }
            onUpdate(trainingId, roundId, end);
        }

        protected abstract void onUpdate(Long trainingId, Long roundId, End end);
    }
}
