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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
