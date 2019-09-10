/*
 * Copyright (C) 2019 Florian Dreier
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

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Shot
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

object Migration27 : Migration(26, 27) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.i("Migrating DB from version 26 to 27")

        database.execSQL(
            "UPDATE RoundTemplate SET shotsPerEnd=3, endCount=10 " +
                    "WHERE standardRoundId IN (SELECT id FROM StandardRound WHERE id = 11)" // Bray I
        )
        database.execSQL(
            "UPDATE RoundTemplate SET shotsPerEnd=3, endCount=10 " +
                    "WHERE standardRoundId IN (SELECT id FROM StandardRound WHERE id = 12)" // Bray II
        )
        database.execSQL(
            "UPDATE Round SET shotsPerEnd=3, maxEndCount=10 " +
                    "WHERE trainingId IN (SELECT t.id FROM Training AS t JOIN StandardRound AS s WHERE s.id = 11)" // Bray I
        )
        database.execSQL(
            "UPDATE Round SET shotsPerEnd=3, maxEndCount=10 " +
                    "WHERE trainingId IN (SELECT t.id FROM Training AS t JOIN StandardRound AS s WHERE s.id = 12)" // Bray II
        )

        migrate(database, 11) // Bray I
        migrate(database, 12) // Bray II
    }

    fun migrate(database: SupportSQLiteDatabase, standardRoundId: Int) {
        database.query("SELECT r.id, `targetId`, `targetScoringStyleIndex`, `targetDiameter` FROM `Round` AS r JOIN `Training` AS t ON r.trainingId = t.id WHERE t.standardRoundId = ${standardRoundId}")
            .useEach { round ->
                val roundId = round.getLong(0)
                val diameter = Dimension.parse(round.getString(3))
                val target = Target(round.getLong(1), round.getInt(2), diameter)

                var index = 0
                val ends = mutableListOf<Long>()
                database.query("SELECT `id`, `index`, `roundId`, `exact`, `saveTime`, `comment` FROM `End` WHERE `roundId` = $roundId")
                    .useEach { end ->
                        val mEnd = End(
                            id = end.getLong(0),
                            index = end.getInt(1),
                            roundId = end.getLong(2),
                            exact = end.getInt(3) == 1,
                            saveTime = LocalTime.parse(end.getString(4)),
                            comment = end.getString(5)
                        )

                        val files = mutableListOf<String>()
                        database.query("SELECT `fileName` FROM `EndImage` WHERE `endId` = ${mEnd.id}")
                            .useEach { image ->
                                files.add(image.getString(0))
                            }

                        ends.add(mEnd.id)

                        addEnd(database, target, mEnd, files, index++)
                        addEnd(database, target, mEnd, files, index++)

                        deleteEndImage(database, mEnd.id)
                        deleteShots(database, mEnd.id)
                    }

                deleteEnds(database, ends.joinToString())
            }
    }

    fun addEnd(database: SupportSQLiteDatabase, target: Target, end: End, files: List<String>, index: Int) {
        val shots = mutableListOf<Shot>()
        database.query("SELECT `id`, `index`, `scoringRing` FROM `Shot` WHERE `endId` = ${end.id} ORDER BY `id` LIMIT 3")
            .useEach { shotCursor ->
                shots.add(
                    Shot(
                        id = shotCursor.getLong(0),
                        index = shotCursor.getInt(1),
                        scoringRing = shotCursor.getInt(2)
                    )
                )
            }

        val score = target.getReachedScore(shots)

        val endId = insertEnd(database, end, score, index)

        files.forEach { fileName ->
            insertEndImage(database, fileName, endId)
        }

        var shotIndex = 0
        shots.forEach { shot ->
            database.execSQL("UPDATE `Shot` SET `endId` = $endId, `index` = $shotIndex WHERE `id` = ${shot.id}")
            shotIndex++
        }
    }

    private fun insertEnd(database: SupportSQLiteDatabase, item: End, score: Score, index: Int): Long {
        val values = ContentValues()
        values.put("`index`", index)
        values.put("roundId", item.roundId)
        values.put("exact", if (item.exact) 1 else 0)
        values.put("saveTime", item.saveTime?.format(DateTimeFormatter.ISO_LOCAL_TIME))
        values.put("comment", item.comment)
        values.put("reachedPoints", score.reachedPoints)
        values.put("totalPoints", score.totalPoints)
        values.put("shotCount", score.shotCount)
        return database.insert("End", SQLiteDatabase.CONFLICT_NONE, values)
    }

    private fun insertEndImage(database: SupportSQLiteDatabase, fileName: String, endId: Long): Long {
        val values = ContentValues()
        values.put("fileName", fileName)
        values.put("endId", endId)
        return database.insert("EndImage", SQLiteDatabase.CONFLICT_NONE, values)
    }

    private fun deleteEndImage(database: SupportSQLiteDatabase, endId: Long) {
        database.delete("EndImage", "endId = ?", arrayOf(endId))
    }

    private fun deleteShots(database: SupportSQLiteDatabase, endId: Long) {
        database.delete("Shot", "endId = ?", arrayOf(endId))
    }

    private fun deleteEnds(database: SupportSQLiteDatabase, ids: String) {
        database.execSQL("DELETE FROM `End` WHERE `id` IN ($ids)")
    }
}