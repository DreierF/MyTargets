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
package de.dreier.mytargets.features.training.overview;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import de.dreier.mytargets.shared.models.IIdProvider;

public class Month implements IIdProvider, Comparable<Month> {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM yyyy",
            Locale.getDefault());
    private long id;

    public Month(long id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return LocalDate.ofEpochDay(getId()).format(dateFormat);
    }

    @Override
    public int compareTo(@NonNull Month another) {
        return (int) (getId() - another.getId());
    }

    public Long getId() {
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
