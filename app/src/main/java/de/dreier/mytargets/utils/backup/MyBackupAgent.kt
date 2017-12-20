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

package de.dreier.mytargets.utils.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import de.dreier.mytargets.features.settings.backup.provider.BackupUtils;

public class MyBackupAgent extends BackupAgentHelper {
    // The name of the SharedPreferences file
    public static final String PREFS = "user_preferences";

    // A key to uniquely identify the set of backup data
    private static final String PREFS_BACKUP_KEY = "prefs";
    private static final String SQLITE_BACKUP_KEY = "sqlite";
    private static final String IMAGES_BACKUP_KEY = "images";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        addHelper(PREFS_BACKUP_KEY, new SharedPreferencesBackupHelper(this, PREFS));
        addHelper(SQLITE_BACKUP_KEY, new DbBackupHelper(this));
        addHelper(IMAGES_BACKUP_KEY,
                new FileBackupHelper(this, BackupUtils.INSTANCE.getImages()));
    }
}
