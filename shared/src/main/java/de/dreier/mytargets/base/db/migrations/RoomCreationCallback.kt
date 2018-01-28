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
import android.arch.persistence.room.RoomDatabase
import de.dreier.mytargets.base.db.dao.StandardRoundDAO
import de.dreier.mytargets.shared.utils.StandardRoundFactory

object RoomCreationCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        fillStandardRound(db)
        createScoreTriggers(db)
    }

    fun fillStandardRound(db: SupportSQLiteDatabase) {
        val standardRounds = StandardRoundFactory.initTable()
        for (standardRound in standardRounds) {
            StandardRoundDAO.saveStandardRound(db, standardRound.standardRound, standardRound.roundTemplates)
        }
    }

    fun createScoreTriggers(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TRIGGER round_sum_score " +
                "AFTER UPDATE ON `End` " +
                "BEGIN " +
                "UPDATE `Round` SET " +
                "reachedPoints = (SELECT SUM(reachedPoints) FROM `End` WHERE round = NEW.round), " +
                "totalPoints = (SELECT SUM(totalPoints) FROM `End` WHERE round = NEW.round), " +
                "shotCount = (SELECT SUM(shotCount) FROM `End` WHERE round = NEW.round) " +
                "WHERE _id = NEW.round;" +
                "END;")

        database.execSQL("CREATE TRIGGER training_sum_score " +
                "AFTER UPDATE ON `Round` " +
                "BEGIN " +
                "UPDATE `Training` SET " +
                "reachedPoints = (SELECT SUM(reachedPoints) FROM `Round` WHERE training = NEW.training), " +
                "totalPoints = (SELECT SUM(totalPoints) FROM `Round` WHERE training = NEW.training), " +
                "shotCount = (SELECT SUM(shotCount) FROM `Round` WHERE training = NEW.training) " +
                "WHERE _id = NEW.training;" +
                "END;")
    }
}
