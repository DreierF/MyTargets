package de.dreier.mytargets;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String PREFS = "user_preferences";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs";
    static final String SQLITE_BACKUP_KEY = "sqlite";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        addHelper(PREFS_BACKUP_KEY, new SharedPreferencesBackupHelper(this, PREFS));
        addHelper(SQLITE_BACKUP_KEY, new DbBackupHelper(this, TargetOpenHelper.DATABASE_NAME));
    }
}
