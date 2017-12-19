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

import de.dreier.mytargets.shared.models.IIdProvider;

public class Header implements IIdProvider, Comparable<Header> {

    private final long id;
    private final String title;

    public Header(long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(@NonNull Header another) {
        return (int) (getId() - another.getId());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Header &&
                getClass().equals(another.getClass()) &&
                id == ((Header) another).id;
    }
}
