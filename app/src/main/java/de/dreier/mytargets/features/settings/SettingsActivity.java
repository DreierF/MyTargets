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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.utils.IntentWrapper;
import im.delight.android.languages.Language;

import static android.support.v7.preference.PreferenceFragmentCompat.ARG_PREFERENCE_ROOT;

public class SettingsActivity extends SimpleFragmentActivityBase implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public static IntentWrapper getIntent(ESettingsScreens subScreen) {
        return new IntentWrapper(SettingsActivity.class)
                .with(ARG_PREFERENCE_ROOT, subScreen.getKey());
    }

    @Override
    public Fragment instantiateFragment() {
        String key = getIntent().getStringExtra(ARG_PREFERENCE_ROOT);
        if (key != null) {
            return ESettingsScreens.from(key).create();
        }
        return ESettingsScreens.MAIN.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE);
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    FragmentManager manager = getSupportFragmentManager();
                    if (manager != null) {
                        SettingsFragmentBase currFrag = (SettingsFragmentBase) manager
                                .findFragmentById(android.R.id.content);

                        currFrag.onFragmentResume();
                    }
                });
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ESettingsScreens screen = ESettingsScreens.from(preferenceScreen.getKey());
        SettingsFragmentBase fragment = screen.create();
        Bundle args = new Bundle();
        args.putString(ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(android.R.id.content, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
