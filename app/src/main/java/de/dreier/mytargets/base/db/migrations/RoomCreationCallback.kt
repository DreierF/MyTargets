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
import androidx.room.RoomDatabase
import de.dreier.mytargets.base.db.StandardRoundFactory
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound

object RoomCreationCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        fillStandardRound(db, "id", "standardRoundId", "targetScoringStyleIndex")
        createScoreTriggers(db)
    }

    fun fillStandardRound(
        db: SupportSQLiteDatabase,
        idColumn: String,
        standardRoundColumn: String,
        scoringStyleColumn: String
    ) {
        val standardRounds = StandardRoundFactory.initTable()
        for (standardRound in standardRounds) {
            saveStandardRound(
                db,
                standardRound.standardRound,
                standardRound.roundTemplates,
                idColumn,
                standardRoundColumn,
                scoringStyleColumn
            )
        }
    }

    private fun saveStandardRound(
        db: SupportSQLiteDatabase,
        standardRound: StandardRound,
        roundTemplates: List<RoundTemplate>,
        idColumn: String,
        standardRoundColumn: String,
        scoringStyleColumn: String
    ) {
        insertStandardRound(db, standardRound, idColumn)
        for (roundTemplate in roundTemplates) {
            roundTemplate.standardRoundId = standardRound.id
            insertRoundTemplate(
                db,
                roundTemplate,
                idColumn,
                standardRoundColumn,
                scoringStyleColumn
            )
        }
    }

    private fun insertStandardRound(
        db: SupportSQLiteDatabase,
        standardRound: StandardRound,
        idColumn: String
    ) {
        db.execSQL(
            "INSERT OR REPLACE INTO StandardRound($idColumn, club, name) VALUES (?,?,?)",
            arrayOf(standardRound.id, standardRound.club, standardRound.name)
        )
    }

    private fun insertRoundTemplate(
        db: SupportSQLiteDatabase,
        roundTemplate: RoundTemplate,
        idColumn: String,
        standardRoundColumn: String,
        scoringStyleColumn: String
    ) {
        db.execSQL(
            "INSERT OR REPLACE INTO RoundTemplate($idColumn, $standardRoundColumn, `index`, " +
                    "shotsPerEnd, endCount, distance, targetId, $scoringStyleColumn, targetDiameter) " +
                    "VALUES (?,?,?,?,?,?,?,?,?)",
            arrayOf(
                roundTemplate.id, roundTemplate.standardRoundId, roundTemplate.index,
                roundTemplate.shotsPerEnd, roundTemplate.endCount,
                "${roundTemplate.distance.value} ${roundTemplate.distance.unit}",
                roundTemplate.targetTemplate.id,
                roundTemplate.targetTemplate.scoringStyleIndex,
                roundTemplate.targetTemplate.diameter.value.toString() + " " +
                        roundTemplate.targetTemplate.diameter.unit
            )
        )
    }

    private fun createScoreTriggers(database: SupportSQLiteDatabase) {
        // Insert Triggers
        database.execSQL(getInsertTrigger("Round", "End", "roundId"))
        database.execSQL(getInsertTrigger("Training", "Round", "trainingId"))

        // Update Triggers
        database.execSQL(getUpdateTrigger("Round", "End", "roundId"))
        database.execSQL(getUpdateTrigger("Training", "Round", "trainingId"))

        // Delete Triggers
        database.execSQL(getDeleteTrigger("Round", "End", "roundId"))
        database.execSQL(getDeleteTrigger("Training", "Round", "trainingId"))
    }

    private fun getInsertTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER insert_${parentTable.toLowerCase()}_sum_score " +
                "AFTER INSERT ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "NEW") +
                "END;"
    }

    private fun getUpdateTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER update_${parentTable.toLowerCase()}_sum_score " +
                "AFTER UPDATE OF reachedPoints, totalPoints, shotCount ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "NEW") +
                "END;"
    }

    private fun getDeleteTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER delete_${parentTable.toLowerCase()}_sum_score " +
                "AFTER DELETE ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "OLD") +
                "END;"
    }

    private fun getUpdateQuery(
        parentTable: String,
        childTable: String,
        joinColumn: String,
        reference: String
    ): String {
        return "UPDATE `$parentTable` SET " +
                "reachedPoints = (SELECT IFNULL(SUM(reachedPoints), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn), " +
                "totalPoints = (SELECT IFNULL(SUM(totalPoints), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn), " +
                "shotCount = (SELECT IFNULL(SUM(shotCount), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn) " +
                "WHERE id = $reference.$joinColumn;"
    }
}
