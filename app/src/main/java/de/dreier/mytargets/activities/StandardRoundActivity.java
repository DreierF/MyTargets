/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;
import android.view.View;

import de.dreier.mytargets.fragments.StandardRoundFragment;

public class StandardRoundActivity extends ItemSelectActivity implements View.OnClickListener {
    @Override
    protected Fragment instantiateFragment() {
        return new StandardRoundFragment();
    }

    @Override
    public void onClick(View v) {
        ((StandardRoundFragment) childFragment).onClick(v);
    }
}
