/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.app.backup.FileBackupHelper;
import android.content.Context;

import de.dreier.mytargets.managers.DatabaseManager;

class DbBackupHelper extends FileBackupHelper {
    public DbBackupHelper(Context ctx) {
        super(ctx, ctx.getDatabasePath(DatabaseManager.DATABASE_NAME).getAbsolutePath());
    }
}