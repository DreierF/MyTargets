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

import de.dreier.mytargets.shared.models.IIdProvider;

public class Month implements IIdProvider, Comparable<Month> {
    public static final String ID = "_id";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private long id;

    public Month(long id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return dateFormat.format(new Date(getId()));
    }

    @Override
    public int compareTo(@NonNull Month another) {
        return (int) ((getId() - another.getId()) / 1000L);
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Month &&
                getClass().equals(another.getClass()) &&
                id == ((Month) another).id;
    }
}
