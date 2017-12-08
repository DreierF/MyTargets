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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.shared.models.TimerSettings;

import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_SHOOT_TIME;
import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_WAIT_TIME;
import static de.dreier.mytargets.features.settings.SettingsManager.KEY_TIMER_WARN_TIME;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.BROADCAST_TIMER_SETTINGS_FROM_REMOTE;

public class TimerSettingsFragment extends SettingsFragmentBase {

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().recreate();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(timerReceiver,
                new IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_REMOTE));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(timerReceiver);
        super.onDestroy();
    }

    @Override
    protected void updateItemSummaries() {
        TimerSettings settings = SettingsManager.getTimerSettings();
        setSecondsSummary(KEY_TIMER_WAIT_TIME, settings.waitTime);
        setSecondsSummary(KEY_TIMER_SHOOT_TIME, settings.shootTime);
        setSecondsSummary(KEY_TIMER_WARN_TIME, settings.warnTime);
        ApplicationInstance.wearableClient.sendTimerSettingsFromLocal(settings);
    }

    private void setSecondsSummary(String key, int value) {
        setSummary(key, getResources().getQuantityString(R.plurals.second, value, value));
    }
}
