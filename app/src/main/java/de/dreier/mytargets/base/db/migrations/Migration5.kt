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

object Migration5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS ARROW")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS ARROW (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "length TEXT," +
                    "material TEXT," +
                    "spine TEXT," +
                    "weight TEXT," +
                    "tip_wight TEXT," +
                    "vanes TEXT," +
                    "nock TEXT," +
                    "comment TEXT," +
                    "thumbnail BLOB," +
                    "image TEXT)"
        )
        database.execSQL("ALTER TABLE ROUND ADD COLUMN arrow INTEGER REFERENCES ARROW ON DELETE SET NULL")
        database.execSQL("ALTER TABLE ROUND ADD COLUMN comment TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN comment TEXT DEFAULT ''")
        database.execSQL("UPDATE ROUND SET target=0 WHERE target=1 OR target=2 OR target=3")
        database.execSQL("UPDATE ROUND SET target=2 WHERE target=5 OR target=6 OR target=7")
        database.execSQL("UPDATE ROUND SET target=3 WHERE target=4")
        database.execSQL("UPDATE ROUND SET target=4 WHERE target=8")
        database.execSQL("UPDATE ROUND SET target=5 WHERE target=9")
        database.execSQL("UPDATE ROUND SET target=6 WHERE target=10")
        database.execSQL(
            "UPDATE SHOOT SET points=2 WHERE _id IN (SELECT s._id " +
                    "FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow " +
                    "WHERE r._id=p.round AND s.passe=p._id " +
                    "AND (r.bow=-2 OR b.type=1) AND s.points=1 AND r.target=3)"
        )
    }
}
