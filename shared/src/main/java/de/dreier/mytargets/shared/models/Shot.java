package de.dreier.mytargets.shared.models;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

@Parcel
public class Shot implements IIdSettable, Comparable<Shot> {
    public static final int NOTHING_SELECTED = -2;
    public int zone = NOTHING_SELECTED;
    public static final int MISS = -1;
    public long passe;
    public float x, y;
    public String comment = "";
    public int arrow = -1;
    public int index;
    protected long id;

    public Shot() {}

    public Shot(int i) {
        index = i;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone) {
            return 0;
        }
        return ((zone > another.zone && another.zone != MISS) || zone == MISS) ? 1 : -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Shot &&
                getClass().equals(another.getClass()) &&
                id == ((Shot) another).id;
    }
}
