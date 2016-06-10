/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.LARGE;
import static de.dreier.mytargets.shared.models.Dimension.MEDIUM;
import static de.dreier.mytargets.shared.models.Dimension.SMALL;
import static de.dreier.mytargets.shared.models.Dimension.XLARGE;

public class TargetOvalBase extends TargetModelBase {

    TargetOvalBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Dimension[]{SMALL, MEDIUM, LARGE, XLARGE};
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
