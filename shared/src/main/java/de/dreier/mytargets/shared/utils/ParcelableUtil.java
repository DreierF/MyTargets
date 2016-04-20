package de.dreier.mytargets.shared.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.parceler.Parcels;

import java.lang.reflect.Array;

public class ParcelableUtil {
    public static class Creator<T> implements Parcelable.Creator<T> {
        private final Class<? extends T> type;

        public Creator(Class<? extends T> type) {
            this.type = type;
        }

        public T createFromParcel(Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(type.getClassLoader()));
        }

        @Override public T[] newArray(int i) {
            return (T[]) Array.newInstance(type, i);
        }
    }

    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // this is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }
}