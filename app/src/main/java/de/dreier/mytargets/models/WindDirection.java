/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import android.content.Context;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IIdProvider;

@Parcel
public class WindDirection implements IIdProvider {

    public long id;
    public String name;

    @ParcelConstructor
    WindDirection(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<WindDirection> getList(Context context) {
        List<WindDirection> list = new ArrayList<>();
        String[] arrays = context.getResources().getStringArray(R.array.wind_directions);
        for (int i = 0; i < arrays.length; i++) {
            list.add(new WindDirection(i, arrays[i]));
        }
        return list;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof WindDirection &&
                getClass().equals(another.getClass()) &&
                id == ((WindDirection) another).id;
    }
}
