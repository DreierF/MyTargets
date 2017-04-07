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
package de.dreier.mytargets.shared.migration;

import android.content.Context;

import de.dreier.mytargets.shared.R;

public class Diameter extends Dimension {
    public static final Diameter MINI = new Diameter(-6, "");
    public static final Diameter SMALL = new Diameter(-5, "");
    public static final Diameter MEDIUM = new Diameter(-4, "");
    public static final Diameter LARGE = new Diameter(-3, "");
    public static final Diameter XLARGE = new Diameter(-2, "");

    public Diameter() {}

    public Diameter(int value, String unit) {
        super(value, unit);
    }

    @Override
    public String toString(Context context) {
        if (unit.isEmpty()) {
            switch (value) {
                case -6:
                    return context.getString(R.string.mini);
                case -5:
                    return context.getString(R.string.small);
                case -4:
                    return context.getString(R.string.medium);
                case -3:
                    return context.getString(R.string.large);
                case -2:
                    return context.getString(R.string.xlarge);
            }
        }
        return super.toString(context);
    }
}
