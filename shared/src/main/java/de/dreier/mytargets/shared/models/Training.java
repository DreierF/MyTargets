/*
 * Copyright (C) 2016 Florian Dreier
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

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class Training implements IIdSettable, Comparable<Training> {
    public String title = "";
    public LocalDate date = new LocalDate();
    public Environment environment;
    public long standardRoundId;
    public long bow;
    public long arrow;
    public boolean arrowNumbering;
    public int timePerPasse;
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Training &&
                getClass().equals(another.getClass()) &&
                id == ((Training) another).id;
    }

    public String getFormattedDate() {
        return DateFormat.getDateInstance().format(date.toDate());
    }

    public String getReachedPoints(List<Round> rounds) {
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        return String.format(Locale.ENGLISH, "%d/%d", reachedPoints, maxPoints);
    }

    @Override
    public int compareTo(@NonNull Training training) {
        if (date.equals(training.date)) {
            return (int) (id - training.id);
        }
        return date.compareTo(training.date);
    }
}
