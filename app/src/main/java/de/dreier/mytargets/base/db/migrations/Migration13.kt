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

object Migration13 : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE PASSE SET exact=1 " +
                "WHERE _id IN (SELECT DISTINCT p._id " +
                "FROM PASSE p, SHOOT s " +
                "WHERE p._id=s.passe " +
                "AND s.x!=0)")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN exact INTEGER DEFAULT 0")
        database.execSQL("UPDATE TRAINING SET exact=1 " +
                "WHERE _id IN (SELECT DISTINCT t._id " +
                "FROM TRAINING t, ROUND r, PASSE p " +
                "WHERE t._id=r.training " +
                "AND r._id=p.round " +
                "AND p.exact=1)")
    }
}
