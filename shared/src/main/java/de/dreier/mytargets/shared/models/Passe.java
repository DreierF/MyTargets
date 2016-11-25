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

import org.joda.time.DateTime;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Passe implements IIdSettable, Comparable<Passe> {

    private long id;
    public int index;
    public long roundId;
    public List<Shot> shots;
    public boolean exact;
    public DateTime saveDate = new DateTime();

    @ParcelConstructor
    public Passe(long id) {
        this.id = id;
    }

    public Passe(int ppp) {
        shots = new ArrayList<>(ppp);
        for (int i = 0; i < ppp; i++) {
            final Shot shot = new Shot();
            shot.index = i;
            shots.add(shot);
        }
    }

    public List<Shot> getSortedShotList() {
        final List<Shot> shots = this.shots;
        Collections.sort(shots);
        return shots;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shots) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id == ((Passe) another).id;
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }

    @Override
    public int compareTo(@NonNull Passe passe) {
        return index - passe.index;
    }
}
