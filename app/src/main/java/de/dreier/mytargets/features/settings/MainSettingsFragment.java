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

package de.dreier.mytargets.features.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;

import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.fragments.AboutFragment;

public class MainSettingsFragment extends SettingsFragmentBase {

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case "pref_about":
                AboutFragment.getIntent().withContext(this).start();
                return true;
            case "pref_licence":
                startActivity(new Intent(getContext(),
                        SimpleFragmentActivityBase.LicencesActivity.class));
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }
}
