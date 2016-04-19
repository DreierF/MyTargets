/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Diameter;

public abstract class CircularTargetBase extends TargetDrawable {

    protected Diameter[] diameters;

    public CircularTargetBase(long id, @StringRes int name) {
        super(id, name);
    }

    @Override
    public Diameter[] getDiameters() {
        return diameters;
    }
}
