/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.dreier.mytargets.shared.migration;

import android.content.Context;
import android.support.annotation.NonNull;

import de.dreier.mytargets.shared.R;

public class DimensionOld implements IIdSettableOld, Comparable<DistanceOld> {
    public static final String METER = "m";
    public static final String CENTIMETER = "cm";
    public static final String YARDS = "yd";
    public static final String INCH = "in";
    public int value;
    public String unit;
    protected long id;

    public DimensionOld() {}

    public DimensionOld(int value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public int compareTo(@NonNull DistanceOld another) {
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
        return another instanceof DimensionOld &&
                getClass().equals(another.getClass()) &&
                id == ((DimensionOld) another).id;
    }
}
