/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;

public class Diameter extends Dimension {

    public Diameter(int value, String unit) {
        super(value, unit);
    }

    public Diameter(Context context, int size) {
        super(-1, context.getString(size));
    }

    @Override
    public String toString() {
        if (value == -1) {
            return unit;
        }
        return super.toString();
    }
}
