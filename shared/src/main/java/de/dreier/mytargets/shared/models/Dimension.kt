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
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.RoundTemplate_Table;
import de.dreier.mytargets.shared.models.db.Round_Table;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.models.db.SightMark_Table;
import de.dreier.mytargets.shared.streamwrapper.Stream;

@Parcel
public class Dimension implements IIdProvider, Comparable<Dimension> {
    @Nullable
    public static final Dimension UNKNOWN = new Dimension(-1, (Unit) null);
    private static final int MINI_VALUE = -6;
    @Nullable
    public static final Dimension MINI = new Dimension(MINI_VALUE, (Unit) null);
    private static final int SMALL_VALUE = -5;
    @Nullable
    public static final Dimension SMALL = new Dimension(SMALL_VALUE, (Unit) null);
    private static final int MEDIUM_VALUE = -4;
    @Nullable
    public static final Dimension MEDIUM = new Dimension(MEDIUM_VALUE, (Unit) null);
    private static final int LARGE_VALUE = -3;
    @Nullable
    public static final Dimension LARGE = new Dimension(LARGE_VALUE, (Unit) null);
    private static final int XLARGE_VALUE = -2;
    @Nullable
    public static final Dimension XLARGE = new Dimension(XLARGE_VALUE, (Unit) null);
    public final float value;
    @Nullable
    public final Unit unit;

    @ParcelConstructor
    public Dimension(float value, @SuppressWarnings("NullableProblems") Unit unit) {
        this.value = value;
        if (value < 0) {
            this.unit = null;
        } else {
            this.unit = unit;
        }
    }

    public Dimension(float value, String unit) {
        this.value = value;
        if (value < 0) {
            this.unit = null;
        } else {
            this.unit = Unit.from(unit);
        }
    }

    /**
     * Returns a list of all distances that are either default values or used somewhere in the app
     *
     * @param distance Distance to add to the list (current selected value)
     * @param unit     Distances are only returned which match the specified unit
     * @return List of distances
     */
    public static List<Dimension> getAll(@NonNull Dimension distance, Unit unit) {
        HashSet<Dimension> distances = new HashSet<>();

        distances.add(Dimension.UNKNOWN);

        // Add currently selected distance to list
        if (distance.unit == unit) {
            distances.add(distance);
        }

        // Get all distances used in Round or SightMark table
        distances.addAll(Stream.of(SQLite
                .select(SightMark_Table.distance)
                .from(SightMark.class)
                .queryList())
                .map(sightSetting -> sightSetting.distance)
                .filter(d -> d.unit == unit)
                .toSet());

        distances.addAll(Stream.of(SQLite
                .select(RoundTemplate_Table.distance)
                .from(RoundTemplate.class)
                .queryList())
                .map(round -> round.distance)
                .filter(d -> d.unit == unit)
                .toSet());

        distances.addAll(Stream.of(SQLite
                .select(Round_Table.distance)
                .from(Round.class)
                .queryList())
                .map(round -> round.distance)
                .filter(d -> d.unit == unit)
                .toSet());

        return new ArrayList<>(distances);
    }

    @Override
    public int compareTo(@NonNull Dimension another) {
        if (unit == another.unit) {
            return (int) (value - another.value);
        } else if (unit == null) {
            return -1;
        } else if (another.unit == null) {
            return 1;
        } else {
            return unit.abbreviation.compareTo(another.unit.abbreviation);
        }
    }

    @NonNull
    @Override
    public String toString() {
        final Context context = SharedApplicationInstance.Companion.getContext();
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

    @NonNull
    public Long getId() {
        return (long) hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dimension dimension = (Dimension) o;
        return value == dimension.value && unit == dimension.unit;
    }

    @Override
    public int hashCode() {
        int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
        return 31 * result + (unit != null ? unit.hashCode() : 0);
    }

    @NonNull
    public Dimension convertTo(@NonNull Unit unit) {
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

        @Nullable
        public static Unit from(@Nullable String unit) {
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

        @Nullable
        public static String toStringHandleNull(@Nullable Unit unit) {
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
