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

import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_SHOOT_TIME;
import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_WAIT_TIME;
import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_WARN_TIME;

public class TimerSettingsFragment extends SettingsFragmentBase {
    @Override
    protected void updateItemSummaries() {
        setSecondsSummary(KEY_TIMER_WAIT_TIME, SettingsManager.getTimerWaitTime());
        setSecondsSummary(KEY_TIMER_SHOOT_TIME, SettingsManager.getTimerShootTime());
        setSecondsSummary(KEY_TIMER_WARN_TIME, SettingsManager.getTimerWarnTime());
    }

    private void setSecondsSummary(String key, int value) {
        setSummary(key, getResources().getQuantityString(R.plurals.second, value, value));
    }
}
