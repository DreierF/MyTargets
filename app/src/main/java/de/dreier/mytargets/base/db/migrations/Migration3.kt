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

object Migration3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN x REAL")
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN y REAL")
        val cur = database.query("SELECT s._id, s.points, r.target " +
                "FROM SHOOT s, PASSE p, ROUND r " +
                "WHERE s.passe=p._id " +
                "AND p.round=r._id")
        if (cur.moveToFirst()) {
            do {
                val shoot = cur.getInt(0)
                database.execSQL("UPDATE SHOOT SET x=0, y=0 WHERE _id=" + shoot)
            } while (cur.moveToNext())
        }
        cur.close()
    }
}
