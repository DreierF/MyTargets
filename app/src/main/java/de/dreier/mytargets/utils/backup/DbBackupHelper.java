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

import android.app.backup.FileBackupHelper;
import android.content.Context;

import de.dreier.mytargets.shared.AppDatabase;

class DbBackupHelper extends FileBackupHelper {
    public DbBackupHelper(Context ctx) {
        super(ctx, ctx.getDatabasePath(AppDatabase.NAME).getAbsolutePath());
    }
}