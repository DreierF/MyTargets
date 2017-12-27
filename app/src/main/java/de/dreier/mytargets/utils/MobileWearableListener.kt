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

import android.os.Parcelable;
import android.support.annotation.NonNull;

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
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedRound;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.utils.ParcelableUtilKt;
import de.dreier.mytargets.shared.wearable.WearableClientBase;

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
            case WearableClientBase.REQUEST_TRAINING_TEMPLATE:
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
        TimerSettings settings = ParcelableUtilKt.unmarshall(data, (Parcelable.Creator<TimerSettings>) TimerSettings.CREATOR);
        SettingsManager.INSTANCE.setTimerSettings(settings);
        ApplicationInstance.wearableClient.sendTimerSettingsFromRemote();
    }

    public void trainingTemplate() {
        Training lastTraining = Stream.of(Training.Companion.getAll())
                .sorted(Collections.reverseOrder())
                .findFirstOrNull();
        if (lastTraining != null && lastTraining.getDate().isEqual(LocalDate.now())) {
            ApplicationInstance.wearableClient.updateTraining(new AugmentedTraining(lastTraining));
        } else {
            Training training = new Training();
            training.setTitle(getString(R.string.training));
            training.setDate(LocalDate.now());
            training.setEnvironment(Environment.Companion.getDefault(SettingsManager.INSTANCE.getIndoor()));
            training.setBowId(SettingsManager.INSTANCE.getBow());
            training.setArrowId(SettingsManager.INSTANCE.getArrow());
            training.setArrowNumbering(false);
            AugmentedTraining aTraining = new AugmentedTraining(training);

            boolean freeTraining = !(lastTraining != null &&
                    lastTraining.getStandardRoundId() != null);
            if (freeTraining) {
                Round round = new Round();
                round.setTarget(SettingsManager.INSTANCE.getTarget());
                round.setShotsPerEnd(SettingsManager.INSTANCE.getShotsPerEnd());
                round.setMaxEndCount(null);
                round.setDistance(SettingsManager.INSTANCE.getDistance());
                aTraining.setRounds(new ArrayList<>());
                aTraining.getRounds().add(new AugmentedRound(round));
            } else {
                aTraining.initRoundsFromTemplate(lastTraining.getStandardRound());
            }
            ApplicationInstance.wearableClient.sendTrainingTemplate(aTraining);
        }
    }

    public void createTraining(@NonNull MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        AugmentedTraining augmentedTraining = ParcelableUtilKt.unmarshall(data, (Parcelable.Creator<AugmentedTraining>) AugmentedTraining.CREATOR);
        Training training = augmentedTraining.toTraining();
        training.save();
        ApplicationInstance.wearableClient.updateTraining(new AugmentedTraining(training));
        ApplicationInstance.wearableClient.sendCreateTrainingFromRemoteBroadcast();
    }

    private void endUpdated(@NonNull MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        AugmentedEnd augmentedEnd = ParcelableUtilKt.unmarshall(data, (Parcelable.Creator<AugmentedEnd>) AugmentedEnd.CREATOR);
        AugmentedRound round = new AugmentedRound(Round.Companion.get(augmentedEnd.getEnd().getRoundId()));
        AugmentedEnd newEnd = getLastEmptyOrCreateNewEnd(round);
        newEnd.getEnd().setExact(false);
        newEnd.setShots(augmentedEnd.getShots());
        newEnd.toEnd().save();

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round.getRound(), newEnd.getEnd());
        ApplicationInstance.wearableClient
                .sendUpdateTrainingFromLocalBroadcast(new AugmentedTraining(Training.Companion.get(round.getRound().getTrainingId())));
    }

    private AugmentedEnd getLastEmptyOrCreateNewEnd(@NonNull AugmentedRound round) {
        if (round.getEnds().isEmpty()) {
            return round.addEnd();
        }
        AugmentedEnd lastEnd = round.getEnds().get(round.getEnds().size() - 1);
        if (lastEnd.isEmpty()) {
            return lastEnd;
        } else {
            return round.addEnd();
        }
    }
}
