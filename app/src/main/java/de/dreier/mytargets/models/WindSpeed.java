/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IdProvider;

public class WindSpeed extends IdProvider {
    static final long serialVersionUID = 61L;

    public final String name;

    private WindSpeed(long id, String name) {
        this.setId(id);
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
}
