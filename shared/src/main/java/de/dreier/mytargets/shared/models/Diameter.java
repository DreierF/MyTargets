/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;

import de.dreier.mytargets.shared.R;

public class Diameter extends Dimension {
    public static final Diameter MINI = new Diameter(-6, "");
    public static final Diameter SMALL = new Diameter(-5, "");
    public static final Diameter MEDIUM = new Diameter(-4, "");
    public static final Diameter LARGE = new Diameter(-3, "");
    public static final Diameter XLARGE = new Diameter(-2, "");

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
