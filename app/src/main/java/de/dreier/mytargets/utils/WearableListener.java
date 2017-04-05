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

import com.google.android.gms.wearable.MessageEvent;

import org.joda.time.LocalDate;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.End$$Parcelable;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.wearable.WearableClientBase;
import de.dreier.mytargets.shared.wearable.WearableListenerServiceBase;
import timber.log.Timber;

import static org.parceler.Parcels.unwrap;

/**
 * Listens for incoming connections of wearable devices.
 * On request the class takes care of creating a new training or
 * adding an end to an existing training.
 */
public class WearableListener extends WearableListenerServiceBase {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Timber.d("onMessageReceived() called with: messageEvent = [" + messageEvent.getPath() + "]");
        switch (messageEvent.getPath()) {
            case WearableClientBase.TRAINING_CREATE:
                createTraining();
                break;
            case WearableClientBase.END_UPDATE:
                endUpdated(messageEvent);
                break;
            default:
                break;
        }
    }

    public void createTraining() {
        Training training = new Training();
        training.title = getString(R.string.training);
        training.date = LocalDate.now();
        training.setEnvironment(Environment.getDefault(SettingsManager.getIndoor()));
        training.bowId = SettingsManager.getBow();
        training.arrowId = SettingsManager.getArrow();
        training.arrowNumbering = false;

        training.save();
        ApplicationInstance.wearableClient.updateTraining(t);
    }

    private void endUpdated(MessageEvent messageEvent) {
        Timber.d("endUpdated() called with: messageEvent = [" + messageEvent + "]");
        byte[] data = messageEvent.getData();
        End end = unwrap(ParcelableUtil.unmarshall(data, End$$Parcelable.CREATOR));
        Round round = Round.get(end.roundId);
        End newEnd = round.addEnd();  //TODO change to take last empty end when merging with #4
        newEnd.exact = false;
        newEnd.setShots(end.getShots());
        newEnd.save();

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round, newEnd);
        ApplicationInstance.wearableClient.sendUpdateTrainingFromLocalBroadcast(Training.get(round.trainingId).ensureLoaded());
    }
}
