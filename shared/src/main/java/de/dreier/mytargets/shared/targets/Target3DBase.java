/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;

import static de.dreier.mytargets.shared.models.Dimension.LARGE;
import static de.dreier.mytargets.shared.models.Dimension.MEDIUM;
import static de.dreier.mytargets.shared.models.Dimension.MINI;
import static de.dreier.mytargets.shared.models.Dimension.SMALL;
import static de.dreier.mytargets.shared.models.Dimension.XLARGE;

public abstract class Target3DBase extends TargetModelBase {

    protected Target3DBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Dimension[]{MINI, SMALL, MEDIUM, LARGE, XLARGE};
        is3DTarget = true;
    }
}
