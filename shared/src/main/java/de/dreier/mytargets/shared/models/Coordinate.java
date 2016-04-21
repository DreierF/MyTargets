package de.dreier.mytargets.shared.models;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcels;

import de.dreier.mytargets.shared.utils.ParcelableUtil;

@Parcel
public class Coordinate implements Parcelable {
    public static final Parcelable.Creator<Coordinate> CREATOR
            = new ParcelableUtil.Creator<>(Coordinate.class);

    public float x;
    public float y;

    public Coordinate() {
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int flags) {
        parcel.writeParcelable(Parcels.wrap(this), flags);
    }
}
