/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Dimension;

public abstract class CircularTargetBase extends Target {

    protected Dimension[] diameters;

    public CircularTargetBase(Context context, long id, @StringRes int name) {
        super(context, id, name);
    }
}