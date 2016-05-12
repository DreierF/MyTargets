package de.dreier.mytargets.shared.utils;

import java.io.IOException;
import java.io.Serializable;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;

public class WearableUtils {
    public static final String STARTED_ROUND = "de/dreier/mytargets/round/started";
    public static final String UPDATE_ROUND = "de/dreier/mytargets/round/update/started";
    public static final String FINISHED_INPUT = "de/dreier/mytargets/passe/finished";
    public static final String STOPPED_ROUND = "de/dreier/mytargets/round/stopped";

    public static <T extends Serializable> byte[] serialize(T p) throws IOException {
        //return ParcelableUtil.marshall(p);
        return SerializationUtils.serialize(p);
    }

    public static NotificationInfo deserializeToInfo(byte[] data)
            throws IOException, ClassNotFoundException {
        return SerializationUtils.deserialize(data);
        //return ParcelableUtil.unmarshall(data, NotificationInfo.CREATOR);
    }

    public static Passe deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        //return ParcelableUtil.unmarshall(data, Passe.CREATOR);
        return SerializationUtils.deserialize(data);
    }
}
