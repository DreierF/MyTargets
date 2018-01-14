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

import de.dreier.mytargets.utils.Utils

class StatisticsSettingsFragment : SettingsFragmentBase() {

    public override fun updateItemSummaries() {
        // Disable file type selection for pre-Kitkat, since they do not support PDF generation
        val shareCategory = preferenceManager.findPreference(KEY_STATISTICS)
        shareCategory.isVisible = Utils.isKitKat

        setDefaultSummary(SettingsManager.KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE)
        setDefaultSummary(SettingsManager.KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY)
    }

    companion object {
        const val KEY_STATISTICS = "statistics"
    }
}
