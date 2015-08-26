package de.dreier.mytargets.shared.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;

public class WearableUtils {
    public static final String STARTED_ROUND = "round/started";
    public static final String UPDATE_ROUND = "update/started";
    public static final String FINISHED_INPUT = "passe/finished";
    public static final String STOPPED_ROUND = "round/stopped";

    public static byte[] serialize(Passe p) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(p);
        os.flush();
        return out.toByteArray();
    }

    public static byte[] serialize(NotificationInfo info) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(info);
        os.flush();
        return out.toByteArray();
    }

    public static NotificationInfo deserializeToInfo(byte[] data)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (NotificationInfo) is.readObject();
    }

    public static Passe deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Passe) is.readObject();
    }
}
