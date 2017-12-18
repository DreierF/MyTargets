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

package de.dreier.mytargets.utils;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.shared.models.TimerSettings;

public class WearSettingsManager {
    private static final String KEY_TIMER_ENABLED = "timer_enabled";
    private static final String KEY_TIMER_WARN_TIME = "timer_warn_time";
    private static final String KEY_TIMER_WAIT_TIME = "timer_wait_time";
    private static final String KEY_TIMER_SHOOT_TIME = "timer_shoot_time";
    private static final String KEY_TIMER_VIBRATE = "timer_vibrate";
    private static final String KEY_TIMER_SOUND = "timer_sound";
    private static final SharedPreferences preferences = ApplicationInstance.Companion
            .getSharedPreferences();

    @NonNull
    public static TimerSettings getTimerSettings() {
        TimerSettings settings = new TimerSettings();
        settings.setEnabled(preferences.getBoolean(KEY_TIMER_ENABLED, false));
        settings.setVibrate(preferences.getBoolean(KEY_TIMER_VIBRATE, false));
        settings.setSound(preferences.getBoolean(KEY_TIMER_SOUND, true));
        settings.setWaitTime(preferences.getInt(KEY_TIMER_WAIT_TIME, 10));
        settings.setShootTime(preferences.getInt(KEY_TIMER_SHOOT_TIME, 120));
        settings.setWarnTime(preferences.getInt(KEY_TIMER_WARN_TIME, 30));
        return settings;
    }

    public static void setTimerSettings(@NonNull TimerSettings settings) {
        preferences
                .edit()
                .putBoolean(KEY_TIMER_ENABLED, settings.getEnabled())
                .putBoolean(KEY_TIMER_VIBRATE, settings.getVibrate())
                .putBoolean(KEY_TIMER_SOUND, settings.getSound())
                .putInt(KEY_TIMER_WAIT_TIME, settings.getWaitTime())
                .putInt(KEY_TIMER_SHOOT_TIME, settings.getShootTime())
                .putInt(KEY_TIMER_WARN_TIME, settings.getWarnTime())
                .apply();
    }
}
