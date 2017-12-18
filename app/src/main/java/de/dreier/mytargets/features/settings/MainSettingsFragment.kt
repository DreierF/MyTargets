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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import im.delight.android.languages.Language;

public class MainSettingsFragment extends SettingsFragmentBase {

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @NonNull String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        if (key.equals(SettingsManager.KEY_LANGUAGE)) {
            Language.setFromPreference(getActivity(), SettingsManager.KEY_LANGUAGE, true);
            getActivity().recreate();
        }
    }
}
