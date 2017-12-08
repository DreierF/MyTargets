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

import android.support.annotation.NonNull;

import de.dreier.mytargets.features.settings.backup.BackupSettingsFragment;

/**
 * All available settings screens. The identifiers implicitly match the keys used in the xml
 * definition.
 */
public enum ESettingsScreens {
    MAIN(MainSettingsFragment.class),
    PROFILE(ProfileSettingsFragment.class),
    OVERVIEW(OverviewSettingsFragment.class),
    INPUT(InputSettingsFragment.class),
    TIMER(TimerSettingsFragment.class),
    STATISTICS(StatisticsSettingsFragment.class),
    SCOREBOARD(ScoreboardSettingsFragment.class),
    BACKUP(BackupSettingsFragment.class);

    private final Class<? extends SettingsFragmentBase> settingsFragment;

    ESettingsScreens(Class<? extends SettingsFragmentBase> settingsFragment) {
        this.settingsFragment = settingsFragment;
    }

    @NonNull
    public static ESettingsScreens from(@NonNull String key) {
        return valueOf(key.toUpperCase());
    }

    public String getKey() {
        return name().toLowerCase();
    }

    public SettingsFragmentBase create() {
        try {
            return settingsFragment.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            // Should never happen, because Fragments should
            // always have a zero argument constructor.
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // Should never happen, because Fragments should
            // always have a public constructor.
        }
        // Otherwise just show main fragment
        return new MainSettingsFragment();
    }
}
