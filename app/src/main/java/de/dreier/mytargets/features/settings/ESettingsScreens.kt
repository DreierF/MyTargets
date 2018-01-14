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

import de.dreier.mytargets.features.settings.backup.BackupSettingsFragment
import java.util.*

/**
 * All available settings screens. The identifiers implicitly match the keys used in the xml
 * definition.
 */
enum class ESettingsScreens {
    MAIN,
    PROFILE,
    OVERVIEW,
    INPUT,
    TIMER,
    STATISTICS,
    SCOREBOARD,
    BACKUP;

    val key: String
        get() = name.toLowerCase(Locale.US)

    fun create(): SettingsFragmentBase {
        return when(this) {
            MAIN -> MainSettingsFragment()
            PROFILE -> ProfileSettingsFragment()
            OVERVIEW -> OverviewSettingsFragment()
            INPUT -> InputSettingsFragment()
            TIMER -> TimerSettingsFragment()
            STATISTICS -> StatisticsSettingsFragment()
            SCOREBOARD -> ScoreboardSettingsFragment()
            BACKUP -> BackupSettingsFragment()
        }
    }

    companion object {
        fun from(key: String): ESettingsScreens {
            return valueOf(key.toUpperCase(Locale.US))
        }
    }
}
