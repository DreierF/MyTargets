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

object Migration17 : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TEMP TABLE IF NOT EXISTS DAIR_TRANSLATION AS " +
                "SELECT  sr._id " +
                "FROM STANDARD_ROUND_TEMPLATE sr " +
                "WHERE sr._id>198 " +
                "AND sr.club<256 " +
                "AND sr.name='DAIR 380'")
        database.execSQL("CREATE TEMP TABLE IF NOT EXISTS SR_TRANSLATION AS " +
                "SELECT  sr._id AS fromSR, (4+sr._id-(SELECT MAX(tr._id) FROM DAIR_TRANSLATION tr WHERE tr._id-4<sr._id)) AS toSR " +
                "FROM STANDARD_ROUND_TEMPLATE sr " +
                "WHERE sr._id>198 " +
                "AND sr.club<256")
        database.execSQL("CREATE TEMP TABLE IF NOT EXISTS R_TRANSLATION AS " +
                "SELECT  r1._id AS fromR, r2._id AS toR " +
                "FROM ROUND_TEMPLATE r1, ROUND_TEMPLATE r2, SR_TRANSLATION sr " +
                "WHERE r1.sid=sr.fromSR " +
                "AND r2.sid=sr.toSR " +
                "AND r1.r_index=r2.r_index")
        database.execSQL("UPDATE ROUND " +
                "SET template = (SELECT toR " +
                "FROM R_TRANSLATION " +
                "WHERE fromR=template) " +
                "WHERE template IN (SELECT fromR FROM R_TRANSLATION)")
        database.execSQL("UPDATE TRAINING " +
                "SET standard_round = (SELECT toSR " +
                "FROM SR_TRANSLATION " +
                "WHERE fromSR=standard_round) " +
                "WHERE standard_round IN (SELECT fromSR FROM SR_TRANSLATION)")
        database.execSQL("DELETE FROM STANDARD_ROUND_TEMPLATE " +
                "WHERE _id IN (SELECT fromSR FROM SR_TRANSLATION)")
        database.execSQL("DELETE FROM ROUND_TEMPLATE " +
                "WHERE _id IN (SELECT fromR FROM R_TRANSLATION)")
        database.execSQL("DROP TABLE DAIR_TRANSLATION")
        database.execSQL("DROP TABLE SR_TRANSLATION")
        database.execSQL("DROP TABLE R_TRANSLATION")
    }
}
