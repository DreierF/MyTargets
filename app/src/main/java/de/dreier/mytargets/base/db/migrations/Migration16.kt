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

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object Migration16 : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ARROW ADD COLUMN diameter TEXT DEFAULT '5.0'")
        database.execSQL("ALTER TABLE ARROW ADD COLUMN diameter_unit TEXT DEFAULT 'mm'")
        database.execSQL("ALTER TABLE PASSE ADD COLUMN save_time INTEGER")
        database.execSQL("UPDATE PASSE " +
                "SET save_time=(" +
                "SELECT t.datum + 43200000 + COUNT(p2._id) * 300000 " +
                "FROM TRAINING t " +
                "JOIN  ROUND r1 ON r1.training = t._id AND r1._id = PASSE.round " +
                "LEFT OUTER JOIN ROUND r2 ON r2.training = t._id " +
                "LEFT OUTER JOIN PASSE p2 ON r2._id = p2.round AND p2._id < PASSE._id " +
                "GROUP BY t._id, t.datum " +
                ")")
    }
}
