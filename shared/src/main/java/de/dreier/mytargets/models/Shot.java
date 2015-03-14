package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Shot extends IdProvider implements Comparable<Shot>, Serializable {
    static final long serialVersionUID = 44L;
    public int passe;
    public int zone;
    public float x, y;
    public String comment;

    public Shot() {
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone)
            return 0;
        return ((zone > another.zone && another.zone != -1) || zone == -1) ? 1 : -1;
    }
}
