package de.dreier.mytargets.shared.models;

import android.support.annotation.NonNull;

public class Shot implements IIdSettable, Comparable<Shot> {
    public static final int NOTHING_SELECTED = -2;
    public static final int MISS = -1;
    public int zone = NOTHING_SELECTED;
    public long passe;
    public float x, y;
    public String comment = "";

    // Is the actual number of the arrow not its index, arrow id or something else
    public String arrow = null;

    // The index of the shot in the containing end
    public int index;
    long id;

    public Shot() {
    }

    public Shot(int i) {
        index = i;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone) {
            return 0;
        } else if (another.zone >= 0 && zone >= 0) {
            return zone - another.zone;
        } else {
            return another.zone - zone;
        }
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
