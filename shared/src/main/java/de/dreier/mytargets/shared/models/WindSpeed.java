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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.R;

public class WindSpeed implements IIdProvider, IImageProvider, Comparable<WindSpeed> {

    public long id;
    public String name;
    public int drawable;

    @ParcelConstructor
    WindSpeed(long id, String name, int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    @NonNull
    public static List<WindSpeed> getList(@NonNull Context context) {
        List<WindSpeed> list = new ArrayList<>();
        list.add(new WindSpeed(0, context.getString(R.string.bft_0),
                R.drawable.ic_bft_0_black_24dp));
        list.add(new WindSpeed(1, context.getString(R.string.bft_1),
                R.drawable.ic_bft_1_black_24dp));
        list.add(new WindSpeed(2, context.getString(R.string.bft_2),
                R.drawable.ic_bft_2_black_24dp));
        list.add(new WindSpeed(3, context.getString(R.string.bft_3),
                R.drawable.ic_bft_3_black_24dp));
        list.add(new WindSpeed(4, context.getString(R.string.bft_4),
                R.drawable.ic_bft_4_black_24dp));
        list.add(new WindSpeed(5, context.getString(R.string.bft_5),
                R.drawable.ic_bft_5_black_24dp));
        list.add(new WindSpeed(6, context.getString(R.string.bft_6),
                R.drawable.ic_bft_6_black_24dp));
        list.add(new WindSpeed(7, context.getString(R.string.bft_7),
                R.drawable.ic_bft_7_black_24dp));
        list.add(new WindSpeed(8, context.getString(R.string.bft_8),
                R.drawable.ic_bft_8_black_24dp));
        list.add(new WindSpeed(9, context.getString(R.string.bft_9),
                R.drawable.ic_bft_9_black_24dp));
        return list;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof WindSpeed &&
                getClass().equals(another.getClass()) &&
                id == ((WindSpeed) another).id;
    }

    @Override
    public Drawable getDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(drawable);
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NonNull WindSpeed windSpeed) {
        return (int) (id - windSpeed.id);
    }
}
