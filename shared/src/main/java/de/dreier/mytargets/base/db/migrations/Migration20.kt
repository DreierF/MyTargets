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

object Migration20 : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE Round SET targetScoringStyle = targetScoringStyle+1 "+
                "WHERE targetScoringStyle > 0 "+
                "AND (targetId < 7 OR targetId = 26 OR targetId = 27)")
        database.execSQL("UPDATE RoundTemplate SET targetScoringStyle = targetScoringStyle+1 "+
                "WHERE targetScoringStyle > 0 "+
                "AND (targetId < 7 OR targetId = 26 OR targetId = 27)")
    }
}
