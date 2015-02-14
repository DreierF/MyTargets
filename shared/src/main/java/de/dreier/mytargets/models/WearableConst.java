package de.dreier.mytargets.models;

import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WearableConst {
    public static final String STARTED_ROUND = "round/started";
    public static final String FINISHED_INPUT = "passe/finished";
    public static final String STOPPED_ROUND = "round/stopped";

    public static final String BUNDLE_ROUND = "round";
    public static final String BUNDLE_MODE = "mode";
    public static final String BUNDLE_BOW = "bow";

    public static byte[] serialize(Round r, boolean mode, Bow bow) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(r);
        os.writeBoolean(mode);
        os.writeObject(bow);
        os.flush();
        return out.toByteArray();
    }

    public static byte[] serialize(Passe p) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(p);
        os.flush();
        return out.toByteArray();
    }

    public static Passe deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Passe) is.readObject();
    }

    public static Bundle deserializeToBundle(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_ROUND, (Serializable) is.readObject());
        bundle.putBoolean(BUNDLE_MODE, is.readBoolean());
        bundle.putSerializable(BUNDLE_BOW, (Serializable) is.readObject());
        return bundle;
    }
}
