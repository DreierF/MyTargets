package de.dreier.mytargets.utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import de.dreier.mytargets.managers.DatabaseManager;

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
        addHelper(SQLITE_BACKUP_KEY, new DbBackupHelper(this, DatabaseManager.DATABASE_NAME));
        addHelper(IMAGES_BACKUP_KEY, new FileBackupHelper(this, DatabaseManager.getInstance(this).getImages()));
    }
}
