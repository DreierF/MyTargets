/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.EnvironmentFragment;

public class EnvironmentActivity extends ItemSelectActivity {
    @Override
    protected Fragment instantiateFragment() {
        return new EnvironmentFragment();
    }
}
