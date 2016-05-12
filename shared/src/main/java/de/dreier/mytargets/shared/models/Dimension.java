/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.support.annotation.NonNull;

import de.dreier.mytargets.shared.R;

public class Dimension implements IIdSettable, Comparable<Distance> {
    public static final String METER = "m";
    public static final String CENTIMETER = "cm";
    public static final String YARDS = "yd";
    public static final String INCH = "in";
    public int value;
    public String unit;
    protected long id;

    public Dimension() {}

    public Dimension(int value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public int compareTo(@NonNull Distance another) {
        if (unit.equals(another.unit)) {
            return (int) (getId() - another.getId());
        }
        return unit.compareTo(another.unit);
    }

    public String toString(Context context) {
        if (value == -1) {
            return context.getString(R.string.unknown);
        } else {
            return value + unit;
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
        return another instanceof Dimension &&
                getClass().equals(another.getClass()) &&
                id == ((Dimension) another).id;
    }
}
