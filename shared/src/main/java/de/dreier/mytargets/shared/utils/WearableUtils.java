package de.dreier.mytargets.shared.utils;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;

public class WearableUtils {
    public static final String STARTED_ROUND = "round/started";
    public static final String UPDATE_ROUND = "update/started";
    public static final String FINISHED_INPUT = "passe/finished";
    public static final String STOPPED_ROUND = "round/stopped";

    public static <T> byte[] serialize(T p) throws IOException {
        return ParcelableUtil.marshall(Parcels.wrap(p));
    }

    public static NotificationInfo deserializeToInfo(byte[] data)
            throws IOException, ClassNotFoundException {

        return Parcels.unwrap(ParcelableUtil.unmarshall(data));
    }

    public static Passe deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return Parcels.unwrap((Parcelable) is.readObject());
    }
}
