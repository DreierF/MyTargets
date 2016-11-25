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

public class RoundTemplate implements IIdSettable {
    public long standardRound;
    public int index;
    public int arrowsPerEnd;
    public Target target;
    public Dimension distance;
    public int endCount;
    public Target targetTemplate;
    long id;

    public int getMaxPoints() {
        return target.getEndMaxPoints(arrowsPerEnd) * endCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id == ((RoundTemplate) another).id;
    }
}
