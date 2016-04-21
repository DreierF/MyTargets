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
public class WindSpeed implements IIdProvider {

    public final long id;
    public final String name;

    @ParcelConstructor
    private WindSpeed(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<WindSpeed> getList(Context context) {
        List<WindSpeed> list = new ArrayList<>();
        String[] arrays = context.getResources().getStringArray(R.array.wind_speeds);
        for (int i = 0; i < arrays.length; i++) {
            list.add(new WindSpeed(i, arrays[i]));
        }
        return list;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof WindSpeed &&
                getClass().equals(another.getClass()) &&
                id == ((WindSpeed) another).id;
    }
}
