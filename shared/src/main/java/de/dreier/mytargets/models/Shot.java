package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

public class Shot implements Comparable<Shot> {
    public int passe;
    public int zone;
    public float x, y;
    public String comment;

    public Shot() {}

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone)
            return 0;
        return ((zone > another.zone && another.zone != -1) || zone == -1) ? 1 : -1;
    }

    public static Shot[] newArray(int ppp) {
        Shot[] arr = new Shot[ppp];
        for (int i = 0; i < arr.length; i++)
            arr[i] = new Shot();
        return arr;
    }
}
