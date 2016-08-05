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
import de.dreier.mytargets.shared.SharedApplicationInstance;

public class Dimension implements IIdProvider, Comparable<Dimension> {
    private static final int MINI_VALUE = -6;
    public static final Dimension MINI = new Dimension(MINI_VALUE, (Unit) null);
    private static final int SMALL_VALUE = -5;
    public static final Dimension SMALL = new Dimension(SMALL_VALUE, (Unit) null);
    private static final int MEDIUM_VALUE = -4;
    public static final Dimension MEDIUM = new Dimension(MEDIUM_VALUE, (Unit) null);
    private static final int LARGE_VALUE = -3;
    public static final Dimension LARGE = new Dimension(LARGE_VALUE, (Unit) null);
    private static final int XLARGE_VALUE = -2;
    public static final Dimension XLARGE = new Dimension(XLARGE_VALUE, (Unit) null);

    public final float value;
    public final Unit unit;

    @ParcelConstructor
    public Dimension(float value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Dimension(float value, String unit) {
        this.value = value;
        if (value < 0) {
            this.unit = null;
        } else {
            this.unit = Unit.from(unit);
        }
    }

    @Override
    public int compareTo(@NonNull Dimension another) {
        if (unit == another.unit) {
            return (int) (getId() - another.getId());
        } else if (unit == null) {
            return -1;
        } else if (another.unit == null) {
            return 1;
        } else {
            return unit.abbreviation.compareTo(another.unit.abbreviation);
        }
    }

    @Override
    public String toString() {
        final Context context = SharedApplicationInstance.getContext();
        if (value == -1) {
            return context.getString(R.string.unknown);
        } else if (unit == null) {
            switch ((int) value) {
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
        return Integer.toString((int) value) + unit.toString();
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
        int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
        return 31 * result + (unit != null ? unit.hashCode() : 0);
    }

    public Dimension convertTo(Unit unit) {
        if (this.unit == null) {
            return new Dimension((8f - this.value) * 4f, Unit.CENTIMETER).convertTo(unit);
        }
        float newValue = value * unit.factor / this.unit.factor;
        return new Dimension(newValue, unit);
    }

    public enum Unit {
        CENTIMETER("cm", 100f),
        INCH("in", 39.3701f),
        METER("m", 1f),
        YARDS("yd", 1.09361f),
        FEET("ft", 3.28084f),
        MILLIMETER("mm", 1000f);

        private final String abbreviation;
        /* factor <units> = 1 meter */
        private final float factor;

        Unit(String abbreviation, float factor) {
            this.abbreviation = abbreviation;
            this.factor = factor;
        }

        public static Unit from(String unit) {
            if (unit == null) {
                return null;
            }
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
                case "mm":
                    return MILLIMETER;
                default:
                    return null;
            }
        }

        public static String toStringHandleNull(Unit unit) {
            if (unit == null) {
                return null;
            }
            return unit.toString();
        }

        @Override
        public String toString() {
            return abbreviation;
        }
    }
}
