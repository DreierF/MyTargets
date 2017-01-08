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

package de.dreier.mytargets.features.training.standardround;

import android.support.v4.app.Fragment;

import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase;

public class EditStandardRoundActivity extends SimpleFragmentActivityBase {

    @Override
    protected Fragment instantiateFragment() {
        return new EditStandardRoundFragment();
    }

}
