/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.settings

import androidx.preference.Preference

class ProfileSettingsFragment : SettingsFragmentBase() {

    public override fun updateItemSummaries() {
        setSummary(SettingsManager.KEY_PROFILE_FIRST_NAME, SettingsManager.profileFirstName)
        setSummary(SettingsManager.KEY_PROFILE_LAST_NAME, SettingsManager.profileLastName)
        setSummary(
            SettingsManager.KEY_PROFILE_BIRTHDAY,
            SettingsManager.profileBirthDayFormatted ?: ""
        )
        setSummary(SettingsManager.KEY_PROFILE_CLUB, SettingsManager.profileClub)
        setSummary(SettingsManager.KEY_PROFILE_LICENCE_NUMBER, SettingsManager.profileLicenceNumber)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference !is DatePreference) {
            super.onDisplayPreferenceDialog(preference)
            return
        }
        val f = DatePreferenceDialogFragmentCompat.newInstance(preference.getKey())
        f.setTargetFragment(this, 0)
        f.show(fragmentManager!!, DIALOG_FRAGMENT_TAG)
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG =
            "androidx.preference.PreferenceFragment.DIALOG"
    }
}
