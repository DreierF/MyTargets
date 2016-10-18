/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.R;

public class WindDirection implements IIdProvider, IImageProvider, Comparable<WindDirection> {

    public long id;
    public String name;
    public int drawable;

    @ParcelConstructor
    WindDirection(long id, String name, int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    public static List<WindDirection> getList(Context context) {
        List<WindDirection> list = new ArrayList<>();
        list.add(new WindDirection(0, context.getString(R.string.front),
                R.drawable.ic_arrow_downward_black_24dp));
        list.add(new WindDirection(1, context.getString(R.string.back),
                R.drawable.ic_arrow_upward_black_24dp));
        list.add(new WindDirection(2, context.getString(R.string.left),
                R.drawable.ic_arrow_right_black_24px));
        list.add(new WindDirection(3, context.getString(R.string.right),
                R.drawable.ic_arrow_left_black_24dp));
        list.add(new WindDirection(4, context.getString(R.string.left_front),
                R.drawable.ic_arrow_bottom_right_black_24dp));
        list.add(new WindDirection(5, context.getString(R.string.right_front),
                R.drawable.ic_arrow_bottom_left_black_24dp));
        list.add(new WindDirection(6, context.getString(R.string.left_back),
                R.drawable.ic_arrow_up_right_black_24dp));
        list.add(new WindDirection(7, context.getString(R.string.right_back),
                R.drawable.ic_arrow_up_left_black_24dp));
        return list;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof WindDirection &&
                getClass().equals(another.getClass()) &&
                id == ((WindDirection) another).id;
    }

    @Override
    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(drawable);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NonNull WindDirection windDirection) {
        return (int) (id - windDirection.id);
    }
}
