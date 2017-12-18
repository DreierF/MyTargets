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

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.training.input.SummaryConfiguration;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.features.training.input.TargetView.EKeyboardType.LEFT;

public class InputSettingsFragment extends SettingsFragmentBase {

    @Override
    protected void updateItemSummaries() {
        setSummary(SettingsManager.KEY_INPUT_SUMMARY_AVERAGE_OF, getAverageOf());
        setSummary(SettingsManager.KEY_INPUT_ARROW_DIAMETER_SCALE, String.format(Utils
                .getCurrentLocale(getContext()), "%.1fx", SettingsManager.INSTANCE
                .getInputArrowDiameterScale()));
        setSummary(SettingsManager.KEY_INPUT_TARGET_ZOOM, String.format(Utils
                .getCurrentLocale(getContext()), "%.1fx", SettingsManager.INSTANCE
                .getInputTargetZoom()));
        setSummary(SettingsManager.KEY_INPUT_KEYBOARD_TYPE,
                SettingsManager.INSTANCE.getInputKeyboardType() == LEFT
                        ? getString(R.string.left_handed) : getString(R.string.right_handed));
        setDefaultSummary(SettingsManager.KEY_AGGREGATION_STRATEGY);
        setDefaultSummary(SettingsManager.KEY_SHOW_MODE);
    }

    private String getAverageOf() {
        final SummaryConfiguration configuration = SettingsManager.INSTANCE
                .getInputSummaryConfiguration();
        switch (configuration.averageScope) {
            case END:
                return getString(R.string.end);
            case TRAINING:
                return getString(R.string.training);
            case ROUND:
            default:
                return getString(R.string.round);
        }
    }
}
