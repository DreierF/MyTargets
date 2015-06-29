/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Dimension extends IdProvider implements Comparable<Distance>,Serializable {
    static final long serialVersionUID = 53L;
    public static final String METER = "m";
    public static final String CENTIMETER = "cm";
    public static final String YARDS = "yd";
    public static final String INCH = "in";
    public int value;
    public String unit;

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

    @Override
    public String toString() {
        return value + unit;
    }
}
