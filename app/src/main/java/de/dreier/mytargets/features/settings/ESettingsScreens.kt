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

package de.dreier.mytargets.features.settings

import de.dreier.mytargets.features.settings.backup.BackupSettingsFragment

/**
 * All available settings screens. The identifiers implicitly match the keys used in the xml
 * definition.
 */
enum class ESettingsScreens constructor(private val settingsFragment: Class<out SettingsFragmentBase>) {
    MAIN(MainSettingsFragment::class.java),
    PROFILE(ProfileSettingsFragment::class.java),
    OVERVIEW(OverviewSettingsFragment::class.java),
    INPUT(InputSettingsFragment::class.java),
    TIMER(TimerSettingsFragment::class.java),
    STATISTICS(StatisticsSettingsFragment::class.java),
    SCOREBOARD(ScoreboardSettingsFragment::class.java),
    BACKUP(BackupSettingsFragment::class.java);

    val key: String
        get() = name.toLowerCase()

    fun create(): SettingsFragmentBase {
        try {
            return settingsFragment.newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
            // Should never happen, because Fragments should
            // always have a zero argument constructor.
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            // Should never happen, because Fragments should
            // always have a public constructor.
        }

        // Otherwise just show main fragment
        return MainSettingsFragment()
    }

    companion object {
        fun from(key: String): ESettingsScreens {
            return valueOf(key.toUpperCase())
        }
    }
}
