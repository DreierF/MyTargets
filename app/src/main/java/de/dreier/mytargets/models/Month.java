/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Month extends IdProvider implements Comparable<Month> {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
    public int reachedPoints, maxPoints;

    public Month(long id) {
        this.id = id;
    }

    @Override
    public long getParentId() {
        return 0;
    }

    @Override
    public String toString() {
        return dateFormat.format(new Date(id));
    }

    @Override
    public int compareTo(@NonNull Month another) {
        return (int) (another.id-id);
    }
}
