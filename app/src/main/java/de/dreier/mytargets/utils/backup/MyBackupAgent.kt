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

package de.dreier.mytargets.utils.backup

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper
import android.app.backup.SharedPreferencesBackupHelper

import de.dreier.mytargets.features.settings.backup.provider.BackupUtils

class MyBackupAgent : BackupAgentHelper() {

    // Allocate a helper and add it to the backup agent
    override fun onCreate() {
        addHelper(PREFS_BACKUP_KEY, SharedPreferencesBackupHelper(this, PREFS))
        addHelper(SQLITE_BACKUP_KEY, DbBackupHelper(this))
        addHelper(IMAGES_BACKUP_KEY,
                FileBackupHelper(this, *BackupUtils.images))
    }

    companion object {
        // The name of the SharedPreferences file
        val PREFS = "user_preferences"

        // A key to uniquely identify the set of backup data
        private val PREFS_BACKUP_KEY = "prefs"
        private val SQLITE_BACKUP_KEY = "sqlite"
        private val IMAGES_BACKUP_KEY = "images"
    }
}
