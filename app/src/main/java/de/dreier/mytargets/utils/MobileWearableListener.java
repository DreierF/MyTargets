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

import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TimerSettings$$Parcelable;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.End$$Parcelable;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.models.db.Training$$Parcelable;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.wearable.WearableClientBase;

import static org.parceler.Parcels.unwrap;

/**
 * Listens for incoming connections of wearable devices.
 * On request the class takes care of creating a new training or
 * adding an end to an existing training.
 */
public class MobileWearableListener extends WearableListenerService {

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        switch (messageEvent.getPath()) {
            case WearableClientBase.TRAINING_CREATE:
                createTraining(messageEvent);
                break;
            case WearableClientBase.END_UPDATE:
                endUpdated(messageEvent);
                break;
            case WearableClientBase.TRAINING_TEMPLATE:
                trainingTemplate();
                break;
            case WearableClientBase.TIMER_SETTINGS:
                timerSettings(messageEvent);
                break;
            default:
                break;
        }
    }

    private void timerSettings(@NonNull MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        TimerSettings settings = unwrap(ParcelableUtil
                .unmarshall(data, TimerSettings$$Parcelable.CREATOR));
        SettingsManager.setTimerSettings(settings);
        ApplicationInstance.wearableClient.sendTimerSettingsFromRemote();
    }

    public void trainingTemplate() {
        Optional<Training> lastTraining = Stream.of(Training.getAll())
                .sorted(Collections.reverseOrder())
                .findFirst();
        if (lastTraining.isPresent() && lastTraining.get().date.isEqual(LocalDate.now())) {
            ApplicationInstance.wearableClient.updateTraining(lastTraining.get().ensureLoaded());
        } else {
            Training training = new Training();
            training.title = getString(R.string.training);
            training.date = LocalDate.now();
            training.setEnvironment(Environment.getDefault(SettingsManager.getIndoor()));
            training.bowId = SettingsManager.getBow();
            training.arrowId = SettingsManager.getArrow();
            training.arrowNumbering = false;

            boolean freeTraining = !(lastTraining.isPresent() &&
                    lastTraining.get().standardRoundId != null);
            if (freeTraining) {
                Round round = new Round();
                round.setTarget(SettingsManager.getTarget());
                round.shotsPerEnd = SettingsManager.getShotsPerEnd();
                round.maxEndCount = null;
                round.distance = SettingsManager.getDistance();
                training.rounds = new ArrayList<>();
                training.rounds.add(round);
            } else {
                training.initRoundsFromTemplate(lastTraining.get().getStandardRound());
            }
            ApplicationInstance.wearableClient.sendTrainingTemplate(training.ensureLoaded());
        }
    }

    public void createTraining(@NonNull MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        Training training = unwrap(ParcelableUtil.unmarshall(data, Training$$Parcelable.CREATOR));
        training.save();
        ApplicationInstance.wearableClient.updateTraining(training);
        ApplicationInstance.wearableClient.sendCreateTrainingFromRemoteBroadcast();
    }

    private void endUpdated(@NonNull MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        End end = unwrap(ParcelableUtil.unmarshall(data, End$$Parcelable.CREATOR));
        Round round = Round.get(end.roundId);
        End newEnd = getLastEmptyOrCreateNewEnd(round);
        newEnd.exact = false;
        newEnd.setShots(end.getShots());
        newEnd.save();

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round, newEnd);
        ApplicationInstance.wearableClient
                .sendUpdateTrainingFromLocalBroadcast(Training.get(round.trainingId)
                        .ensureLoaded());
    }

    private End getLastEmptyOrCreateNewEnd(@NonNull Round round) {
        if (round.getEnds().isEmpty()) {
            return round.addEnd();
        }
        End lastEnd = round.getEnds().get(round.getEnds().size() - 1);
        if (lastEnd.isEmpty()) {
            return lastEnd;
        } else {
            return round.addEnd();
        }
    }
}
