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
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.base.db.StandardRoundFactory

object RoomCreationCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        fillStandardRound(db)
        createScoreTriggers(db)
    }

    fun fillStandardRound(db: SupportSQLiteDatabase) {
        val standardRounds = StandardRoundFactory.initTable()
        for (standardRound in standardRounds) {
            saveStandardRound(db, standardRound.standardRound, standardRound.roundTemplates)
        }
    }

    private fun saveStandardRound(db: SupportSQLiteDatabase, standardRound: StandardRound,
                                  roundTemplates: List<RoundTemplate>) {
        insertStandardRound(db, standardRound)
        for (roundTemplate in roundTemplates) {
            roundTemplate.standardRound = standardRound.id
            insertRoundTemplate(db, roundTemplate)
        }
    }

    private fun insertStandardRound(db: SupportSQLiteDatabase, standardRound: StandardRound) {
        db.execSQL("INSERT OR REPLACE INTO StandardRound(_id, club, name) VALUES (?,?,?)",
                arrayOf(standardRound.id, standardRound.club, standardRound.name))
    }

    private fun insertRoundTemplate(db: SupportSQLiteDatabase, roundTemplate: RoundTemplate) {
        db.execSQL("INSERT OR REPLACE INTO RoundTemplate(_id, standardRound, `index`, " +
                "shotsPerEnd, endCount, distance, targetId, targetScoringStyle, targetDiameter) " +
                "VALUES (?,?,?,?,?,?,?,?,?)",
                arrayOf(roundTemplate.id, roundTemplate.standardRound, roundTemplate.index,
                        roundTemplate.shotsPerEnd, roundTemplate.endCount,
                        "${roundTemplate.distance.value} ${roundTemplate.distance.unit}",
                        roundTemplate.targetTemplate.id,
                        roundTemplate.targetTemplate.scoringStyleIndex,
                        roundTemplate.targetTemplate.diameter.value.toString() + " " +
                                roundTemplate.targetTemplate.diameter.unit))
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
