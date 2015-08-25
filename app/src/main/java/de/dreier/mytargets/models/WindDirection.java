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

public class WindDirection extends IdProvider {
    static final long serialVersionUID = 62L;

    public final String name;

    private WindDirection(long id, String name) {
        this.setId(id);
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
}
