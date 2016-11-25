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

public class Round implements IIdSettable, Comparable<Round> {
    long id;
    public long trainingId;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id == ((Round) another).id;
    }

    public String getReachedPointsFormatted() {
        final int maxPoints = info.getMaxPoints();
        return reachedPoints + "/" + maxPoints;
    }

    @Override
    public int compareTo(@NonNull Round round) {
        return info.index - round.info.index;
    }
}
