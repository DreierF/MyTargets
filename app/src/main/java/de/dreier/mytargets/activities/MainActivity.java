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

import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.MainFragment;
import de.dreier.mytargets.utils.TranslationUtils;

/**
 * Shows an overview over all training days
 */
public class MainActivity extends SimpleFragmentActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_CustomToolbar);
        super.onCreate(savedInstanceState);
        TranslationUtils.askForHelpTranslating(this);
    }

    @Override
    public Fragment instantiateFragment() {
        return new MainFragment();
    }

}
