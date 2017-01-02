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

import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.views.DatePreference;
import de.dreier.mytargets.views.DatePreferenceDialogFragmentCompat;

import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_BIRTHDAY;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_CLUB;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_FIRST_NAME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_LAST_NAME;

public class ScoreboardSettingsFragment extends SettingsFragmentBase {

    private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    @Override
    public void updateItemSummaries() {
        setSummary(KEY_PROFILE_FIRST_NAME, SettingsManager.getProfileFirstName());
        setSummary(KEY_PROFILE_LAST_NAME, SettingsManager.getProfileLastName());
        setSummary(KEY_PROFILE_BIRTHDAY, SettingsManager.getProfileBirthDayFormatted());
        setSummary(KEY_PROFILE_CLUB, SettingsManager.getProfileClub());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!(preference instanceof DatePreference)) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        DialogFragment f = DatePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }
}
