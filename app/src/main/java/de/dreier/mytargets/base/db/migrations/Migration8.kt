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

package de.dreier.mytargets.base.db.migrations

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object Migration8 : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE ROUND SET target=4 WHERE target=8")
        database.execSQL("UPDATE ROUND SET target=5 WHERE target=9")
        database.execSQL("UPDATE ROUND SET target=6 WHERE target=10")
    }
}
