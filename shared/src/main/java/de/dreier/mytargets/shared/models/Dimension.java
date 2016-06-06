/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.support.annotation.NonNull;

import org.parceler.ParcelConstructor;

import de.dreier.mytargets.shared.R;

public class Dimension implements IIdProvider, Comparable<Dimension> {
    private static final int MINI_VALUE = -6;
    public static final Dimension MINI = new Dimension(MINI_VALUE, (Unit)null);
    private static final int SMALL_VALUE = -5;
    public static final Dimension SMALL = new Dimension(SMALL_VALUE, (Unit)null);
    private static final int MEDIUM_VALUE = -4;
    public static final Dimension MEDIUM = new Dimension(MEDIUM_VALUE, (Unit)null);
    private static final int LARGE_VALUE = -3;
    public static final Dimension LARGE = new Dimension(LARGE_VALUE, (Unit)null);
    private static final int XLARGE_VALUE = -2;
    public static final Dimension XLARGE = new Dimension(XLARGE_VALUE, (Unit)null);

    public int value;
    public Unit unit;

    @ParcelConstructor
    public Dimension(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Dimension(int value, String unit) {
        this.value = value;
        this.unit = Unit.from(unit);
    }

    @Override
    public int compareTo(@NonNull Dimension another) {
        if (unit.equals(another.unit)) {
            return (int) (getId() - another.getId());
        }
        return unit.compareTo(another.unit);
    }

    public String toString(Context context) {
        if (value == -1) {
            return context.getString(R.string.unknown);
        } else if (unit == null) {
            switch (value) {
                case MINI_VALUE:
                    return context.getString(R.string.mini);
                case SMALL_VALUE:
                    return context.getString(R.string.small);
                case MEDIUM_VALUE:
                    return context.getString(R.string.medium);
                case LARGE_VALUE:
                    return context.getString(R.string.large);
                case XLARGE_VALUE:
                    return context.getString(R.string.xlarge);
                default:
                    return "";
            }
        }
        return value + unit.toString();
    }

    public long getId() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return value == dimension.value && unit == dimension.unit;

    }

    @Override
    public int hashCode() {
        return 31 * value + (unit != null ? unit.hashCode() : 0);
    }

    public enum Unit {
        CENTIMETER("cm"),
        INCH("in"),
        METER("m"),
        YARDS("yd"),
        FEET("ft");

        private final String abbreviation;

        Unit(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public static Unit from(String unit) {
            switch (unit) {
                case "cm":
                    return CENTIMETER;
                case "in":
                    return INCH;
                case "m":
                    return METER;
                case "yd":
                    return YARDS;
                case "ft":
                    return FEET;
            }
            return null;
        }

        @Override
        public String toString() {
            return abbreviation;
        }
    }
}
