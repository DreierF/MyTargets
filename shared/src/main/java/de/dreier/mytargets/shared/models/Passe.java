package de.dreier.mytargets.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.shared.utils.ParcelableUtil;

@org.parceler.Parcel
public class Passe implements IIdSettable, Parcelable {
    public static final ParcelableUtil.Creator<Passe> CREATOR
            = new ParcelableUtil.Creator<>(Passe.class);

    protected long id;
    public int index;
    public long roundId;
    public Shot[] shot;
    public boolean exact;

    public Passe() {
    }

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot(i);
        }
    }

    public Passe(Passe p) {
        this.id = p.getId();
        shot = p.shot.clone();
    }

    public List<Shot> shotList() {
        return Arrays.asList(shot);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(Parcels.wrap(this), flags);
    }

    public void sort() {
        Arrays.sort(shot);
        for (int i = 0; i < shot.length; i++) {
            shot[i].index = i;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shot) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id == ((Passe) another).id;
    }
}
