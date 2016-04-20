/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Diameter.LARGE;
import static de.dreier.mytargets.shared.models.Diameter.MEDIUM;
import static de.dreier.mytargets.shared.models.Diameter.MINI;
import static de.dreier.mytargets.shared.models.Diameter.SMALL;
import static de.dreier.mytargets.shared.models.Diameter.XLARGE;

public abstract class Target3DBase extends TargetModelBase {

    public Target3DBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Diameter[]{MINI, SMALL, MEDIUM, LARGE, XLARGE};
        this.is3DTarget = true;
    }
}
