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

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.wearable.MessageEvent;

import org.joda.time.LocalDate;

import de.dreier.mytargets.app.ApplicationInstance;
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
            ApplicationInstance.wearableClient.updateTraining(t);
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

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round, newEnd);
    }

}
