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

import android.support.v7.preference.Preference;

import de.dreier.mytargets.utils.Utils;

public class ScoreboardSettingsFragment extends SettingsFragmentBase {

    public static final String KEY_SCOREBOARD_SHARE = "scoreboard_share";

    @Override
    public void updateItemSummaries() {
        // Disable file type selection for pre-Kitkat, since they do not support PDF generation
        Preference shareCategory = getPreferenceManager().findPreference(KEY_SCOREBOARD_SHARE);
        shareCategory.setVisible(Utils.isKitKat());
        Preference shareFileType = getPreferenceManager().findPreference(SettingsManager
                .KEY_SCOREBOARD_SHARE_FILE_TYPE);
        shareFileType.setVisible(Utils.isKitKat());
        if (Utils.isKitKat()) {
            setDefaultSummary(SettingsManager.KEY_SCOREBOARD_SHARE_FILE_TYPE);
        }
    }
}
