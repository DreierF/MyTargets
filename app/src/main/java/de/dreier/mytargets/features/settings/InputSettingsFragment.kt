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

import de.dreier.mytargets.R
import de.dreier.mytargets.features.training.input.ETrainingScope
import de.dreier.mytargets.features.training.input.TargetView.EKeyboardType.LEFT
import de.dreier.mytargets.utils.Utils

class InputSettingsFragment : SettingsFragmentBase() {

    private val averageOf: String
        get() {
            val configuration = SettingsManager.inputSummaryConfiguration
            return when (configuration.averageScope) {
                ETrainingScope.END -> getString(R.string.end)
                ETrainingScope.TRAINING -> getString(R.string.training)
                ETrainingScope.ROUND -> getString(R.string.round)
            }
        }

    override fun updateItemSummaries() {
        setSummary(SettingsManager.KEY_INPUT_SUMMARY_AVERAGE_OF, averageOf)
        setSummary(SettingsManager.KEY_INPUT_ARROW_DIAMETER_SCALE, String.format(Utils
                .getCurrentLocale(context!!), "%.1fx", SettingsManager
                .inputArrowDiameterScale))
        setSummary(SettingsManager.KEY_INPUT_TARGET_ZOOM, String.format(Utils
                .getCurrentLocale(context!!), "%.1fx", SettingsManager
                .inputTargetZoom))
        setSummary(SettingsManager.KEY_INPUT_KEYBOARD_TYPE,
                if (SettingsManager.inputKeyboardType == LEFT)
                    getString(R.string.left_handed)
                else
                    getString(R.string.right_handed))
        setDefaultSummary(SettingsManager.KEY_AGGREGATION_STRATEGY)
        setDefaultSummary(SettingsManager.KEY_SHOW_MODE)
    }
}
