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

package de.dreier.mytargets.features.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.utils.TranslationUtils;

/**
 * Shows an overview over all training days
 */
public class MainActivity extends SimpleFragmentActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_CustomToolbar);
        super.onCreate(savedInstanceState);
        if (SettingsManager.shouldShowIntroActivity()) {
            SettingsManager.setShouldShowIntroActivity(false);
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        } else {
            TranslationUtils.askForHelpTranslating(this);
        }
    }

    @Override
    public Fragment instantiateFragment() {
        return new MainFragment();
    }

}