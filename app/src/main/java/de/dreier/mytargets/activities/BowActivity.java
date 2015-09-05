/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.BowFragment;

public class BowActivity extends ItemSelectActivity {

    @Override
    public Fragment instantiateFragment() {
        return new BowFragment();
    }
}
