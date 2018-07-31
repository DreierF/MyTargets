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

object Migration11 : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ARROW ADD COLUMN tip_weight TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE PASSE ADD COLUMN exact INTEGER DEFAULT 0")
        database.execSQL("UPDATE PASSE SET exact=1 WHERE _id IN (SELECT DISTINCT p._id FROM PASSE p, SHOOT s WHERE p._id=s.passe AND s.x!=0)")
    }
}
