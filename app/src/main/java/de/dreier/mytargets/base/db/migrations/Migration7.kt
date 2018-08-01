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

object Migration7 : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM ROUND WHERE _id IN (SELECT r._id FROM ROUND r LEFT JOIN TRAINING t ON t._id=r.training WHERE t._id IS NULL)")
        database.execSQL("DELETE FROM PASSE WHERE _id IN (SELECT p._id FROM PASSE p LEFT JOIN ROUND r ON r._id=p.round WHERE r._id IS NULL)")
        database.execSQL("DELETE FROM SHOOT WHERE _id IN (SELECT s._id FROM SHOOT s LEFT JOIN PASSE p ON p._id=s.passe WHERE p._id IS NULL)")
    }
}
