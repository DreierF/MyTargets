package de.dreier.mytargets.shared.utils;

import android.os.Parcelable;

import java.io.IOException;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;

public class WearableUtils {
    public static final String STARTED_ROUND = "round/started";
    public static final String UPDATE_ROUND = "update/started";
    public static final String FINISHED_INPUT = "passe/finished";
    public static final String STOPPED_ROUND = "round/stopped";

    public static <T extends Parcelable> byte[] serialize(T p) throws IOException {
        return ParcelableUtil.marshall(p);
    }

    public static NotificationInfo deserializeToInfo(byte[] data)
            throws IOException, ClassNotFoundException {
        return ParcelableUtil.unmarshall(data, NotificationInfo.CREATOR);
    }

    public static Passe deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        return ParcelableUtil.unmarshall(data, Passe.CREATOR);
    }
}
