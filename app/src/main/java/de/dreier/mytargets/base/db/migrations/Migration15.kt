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

object Migration15 : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE SHOOT SET arrow = NULL WHERE arrow = '-1'")
        database.execSQL(
            "UPDATE PASSE SET exact = 1 WHERE _id IN (SELECT DISTINCT p._id " +
                    "FROM PASSE p, SHOOT s " +
                    "WHERE p._id = s.passe " +
                    "AND s.x != 0)"
        )
    }
}
