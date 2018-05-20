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

object Migration4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS VISIER ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "bow REFERENCES BOW ON DELETE CASCADE," +
                    "distance INTEGER," +
                    "setting TEXT);"
        )
        val valuesMetric = intArrayOf(10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90)
        for (table in arrayOf("ROUND", "VISIER")) {
            for (i in 10 downTo 0) {
                database.execSQL(
                    "UPDATE " + table + " SET distance=" +
                            valuesMetric[i] + " WHERE distance=" + i
                )
            }
        }
        database.execSQL("ALTER TABLE BOW ADD COLUMN height TEXT DEFAULT '';")
    }
}
