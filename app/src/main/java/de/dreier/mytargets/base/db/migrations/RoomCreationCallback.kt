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
import de.dreier.mytargets.base.db.StandardRoundFactory
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound

object RoomCreationCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        fillStandardRound(db, "id", "standardRoundId")
        createScoreTriggers(db)
    }

    fun fillStandardRound(
        db: SupportSQLiteDatabase,
        idColumn: String,
        standardRoundColumn: String
    ) {
        val standardRounds = StandardRoundFactory.initTable()
        for (standardRound in standardRounds) {
            saveStandardRound(
                db,
                standardRound.standardRound,
                standardRound.roundTemplates,
                idColumn,
                standardRoundColumn
            )
        }
    }

    private fun saveStandardRound(
        db: SupportSQLiteDatabase,
        standardRound: StandardRound,
        roundTemplates: List<RoundTemplate>,
        idColumn: String,
        standardRoundColumn: String
    ) {
        insertStandardRound(db, standardRound, idColumn)
        for (roundTemplate in roundTemplates) {
            roundTemplate.standardRoundId = standardRound.id
            insertRoundTemplate(db, roundTemplate, idColumn, standardRoundColumn)
        }
    }

    private fun insertStandardRound(db: SupportSQLiteDatabase, standardRound: StandardRound, idColumn: String) {
        db.execSQL("INSERT OR REPLACE INTO StandardRound($idColumn, club, name) VALUES (?,?,?)",
                arrayOf(standardRound.id, standardRound.club, standardRound.name))
    }

    private fun insertRoundTemplate(
        db: SupportSQLiteDatabase,
        roundTemplate: RoundTemplate,
        idColumn: String,
        standardRoundColumn: String
    ) {
        db.execSQL("INSERT OR REPLACE INTO RoundTemplate($idColumn, $standardRoundColumn, `index`, " +
                "shotsPerEnd, endCount, distance, targetId, targetScoringStyle, targetDiameter) " +
                "VALUES (?,?,?,?,?,?,?,?,?)",
                arrayOf(roundTemplate.id, roundTemplate.standardRoundId, roundTemplate.index,
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
                "reachedPoints = (SELECT SUM(reachedPoints) FROM `End` WHERE roundId = NEW.roundId), " +
                "totalPoints = (SELECT SUM(totalPoints) FROM `End` WHERE roundId = NEW.roundId), " +
                "shotCount = (SELECT SUM(shotCount) FROM `End` WHERE roundId = NEW.roundId) " +
                "WHERE id = NEW.roundId;" +
                "END;")

        database.execSQL("CREATE TRIGGER training_sum_score " +
                "AFTER UPDATE ON `Round` " +
                "BEGIN " +
                "UPDATE `Training` SET " +
                "reachedPoints = (SELECT SUM(reachedPoints) FROM `Round` WHERE trainingId = NEW.trainingId), " +
                "totalPoints = (SELECT SUM(totalPoints) FROM `Round` WHERE trainingId = NEW.trainingId), " +
                "shotCount = (SELECT SUM(shotCount) FROM `Round` WHERE trainingId = NEW.trainingId) " +
                "WHERE id = NEW.trainingId;" +
                "END;")
    }
}
