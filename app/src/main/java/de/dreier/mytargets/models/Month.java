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

import de.dreier.mytargets.shared.models.IdProvider;

public class Month extends IdProvider implements Comparable<Month> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    public Month(long id) {
        this.setId(id);
    }

    @Override
    public long getParentId() {
        return 0;
    }

    @Override
    public String toString() {
        return dateFormat.format(new Date(getId()));
    }

    @Override
    public int compareTo(@NonNull Month another) {
        return (int) (getId() - another.getId());
    }
}
