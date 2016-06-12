/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

@Parcel
public class ArrowNumber implements IIdSettable {
    public int number;
    long id;

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof ArrowNumber &&
                getClass().equals(another.getClass()) &&
                id == ((ArrowNumber) another).id;
    }
}
