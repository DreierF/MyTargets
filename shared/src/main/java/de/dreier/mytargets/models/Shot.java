package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Shot extends IdProvider implements Comparable<Shot>, Serializable {
    static final long serialVersionUID = 44L;
    public static final int NOTHING_SELECTED = -2;
    public static final int MISS = -1;

    public int passe;
    public int zone = NOTHING_SELECTED;
    public float x, y;
    public String comment;

    public Shot() {
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone)
            return 0;
        return ((zone > another.zone && another.zone != MISS) || zone == MISS) ? 1 : -1;
    }
}
