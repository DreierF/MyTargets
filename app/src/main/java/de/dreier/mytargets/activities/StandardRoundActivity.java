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
package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;
import android.view.View;

import de.dreier.mytargets.fragments.StandardRoundListFragment;

public class StandardRoundActivity extends ItemSelectActivity implements View.OnClickListener {
    @Override
    protected Fragment instantiateFragment() {
        return new StandardRoundListFragment();
    }

    @Override
    public void onClick(View v) {
        ((StandardRoundListFragment) childFragment).onClick(v);
    }
}
