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

object Migration19 : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE Round SET distance=\"-1 m\" WHERE distance IS NULL")
        database.execSQL("UPDATE Round SET targetDiameter=\"-1 cm\" WHERE targetDiameter IS NULL")
        database.execSQL("UPDATE RoundTemplate SET distance=\"-1 m\" WHERE distance IS NULL")
        database.execSQL("UPDATE RoundTemplate SET targetDiameter=\"-1 cm\" WHERE targetDiameter IS NULL")
    }
}
