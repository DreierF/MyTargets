package de.dreier.mytargets.shared.models;

import android.support.annotation.NonNull;

public class Shot extends IdProvider implements Comparable<Shot> {
    static final long serialVersionUID = 57L;

    public static final int NOTHING_SELECTED = -2;
    public int zone = NOTHING_SELECTED;
    public static final int MISS = -1;
    public long passe;
    public float x, y;
    public String comment = "";
    public int arrow = -1;
    public int index;

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
}
