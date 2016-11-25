/*
 * Copyright (C) 2016 Florian Dreier
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
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;

import static de.dreier.mytargets.shared.models.Dimension.LARGE;
import static de.dreier.mytargets.shared.models.Dimension.MEDIUM;
import static de.dreier.mytargets.shared.models.Dimension.SMALL;
import static de.dreier.mytargets.shared.models.Dimension.XLARGE;

public class TargetOvalBase extends TargetModelBase {

    protected TargetOvalBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Dimension[]{SMALL, MEDIUM, LARGE, XLARGE};
    }

    @Override
    public boolean dependsOnArrowIndex() {
        return true;
    }

}
