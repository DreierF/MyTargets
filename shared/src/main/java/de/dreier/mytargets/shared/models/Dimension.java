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
    public int distance;
    public String unit;

    public Dimension(int distance, String unit) {
        this.distance = distance;
        this.unit = unit;
    }

    @Override
    public int compareTo(@NonNull Distance another) {
        if (unit.equals(another.unit)) {
            return (int) (id - another.id);
        }
        return unit.compareTo(another.unit);
    }

    @Override
    public String toString() {
        return distance + unit;
    }
}
