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
import static de.dreier.mytargets.shared.models.Diameter.SMALL;
import static de.dreier.mytargets.shared.models.Diameter.XLARGE;

public class TargetOvalBase extends TargetModelBase {

    public TargetOvalBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Diameter[]{SMALL, MEDIUM, LARGE, XLARGE};
    }

    @Override
    public boolean dependsOnArrowIndex() {
        return true;
    }

    @Override
    public boolean is3DTarget() {
        return true;
    }
}
