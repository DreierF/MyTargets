package de.dreier.mytargets.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WearableUtils {
    public static final String STARTED_ROUND = "round/started";
    public static final String UPDATE_ROUND = "update/started";
    public static final String FINISHED_INPUT = "passe/finished";
    public static final String STOPPED_ROUND = "round/stopped";

    public static final String BUNDLE_IMAGE = "bow";
    public static final String BUNDLE_INFO = "info";

    public static byte[] serialize(Bitmap image, NotificationInfo info) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        serializeBitmap(os, image);
        os.writeObject(info);
        os.flush();
        return out.toByteArray();
    }

    private static void serializeBitmap(ObjectOutputStream os, Bitmap image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        BitmapDataObject bitmapDataObject = new BitmapDataObject();
        bitmapDataObject.imageByteArray = stream.toByteArray();
        os.writeObject(bitmapDataObject);
    }

    public static byte[] serialize(Shot[] p) throws IOException {
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

    public static NotificationInfo deserializeToInfo(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (NotificationInfo) is.readObject();
    }

    public static Shot[] deserializeToPasse(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Shot[]) is.readObject();
    }

    public static Bundle deserializeToBundle(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_IMAGE, (BitmapDataObject) is.readObject());
        bundle.putSerializable(BUNDLE_INFO, (NotificationInfo) is.readObject());
        return bundle;
    }

    public static class NotificationInfo implements Serializable {
        static final long serialVersionUID = 43L;

        public NotificationInfo(Round round, String title, String text) {
            this.round = round;
            this.title = title;
            this.text = text;
        }

        public String title;
        public String text;
        public Round round;
    }

    public static class BitmapDataObject implements Serializable {
        private static final long serialVersionUID = 111696345129311948L;
        private byte[] imageByteArray;

        public BitmapDataObject() {
        }

        public Bitmap getBitmap() {
            return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        }
    }
}
