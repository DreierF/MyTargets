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

package de.dreier.mytargets.shared.utils;

import android.os.Parcel;
import android.os.Parcelable;

import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;

public class ParcelableUtil {

    private static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static TrainingInfo unmarshallTrainingInfo(byte[] bytes) {
        return unmarshall(bytes, (Parcelable.Creator<TrainingInfo>) TrainingInfo.CREATOR);
    }

    public static AugmentedTraining unmarshallAugmentedTraining(byte[] bytes) {
        return unmarshall(bytes, (Parcelable.Creator<AugmentedTraining>) AugmentedTraining.CREATOR);
    }

    public static AugmentedEnd unmarshallAugmentedEnd(byte[] bytes) {
        return unmarshall(bytes, (Parcelable.Creator<AugmentedEnd>) AugmentedEnd.CREATOR);
    }

    public static TimerSettings unmarshallTimerSettings(byte[] bytes) {
        return unmarshall(bytes, (Parcelable.Creator<TimerSettings>) TimerSettings.CREATOR);
    }
}
