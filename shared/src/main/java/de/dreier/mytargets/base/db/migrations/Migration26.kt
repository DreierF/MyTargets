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
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot

object Migration26 : Migration(25, 26) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `Training` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN shotCount INTEGER;")

        database.execSQL("ALTER TABLE `Round` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN shotCount INTEGER;")

        database.execSQL("ALTER TABLE `End` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN shotCount INTEGER;")

        RoomCreationCallback.createScoreTriggers(database)

        val rounds = database.query("SELECT _id, targetId, targetScoringStyle, targetDiameter " +
                "FROM Round")

        while (rounds.moveToNext()) {
            val diameterData = rounds.getString(3)
            val index = diameterData.indexOf(' ')
            val value = diameterData.substring(0, index)
            val unit = diameterData.substring(index + 1)
            val diameter = Dimension.from(value.toFloat(), unit)
            val target = Target(rounds.getLong(1), rounds.getInt(2), diameter)
            val roundId = rounds.getLong(0)

            val ends = database.query("SELECT _id, FROM `End` WHERE round = $roundId")
            while (ends.moveToNext()) {
                val endId = ends.getLong(0)

                val shotsCursor = database.query("SELECT index, scoringRing FROM Shot WHERE end = $endId ORDER BY `index`")
                val shots = mutableListOf<Shot>()
                while(shotsCursor.moveToNext()) {
                    val shot = Shot(index = shotsCursor.getInt(0), scoringRing = shotsCursor.getInt(1))
                    shots.add(shot)
                }

                val score = target.getReachedScore(shots)
                database.execSQL("UPDATE `End` SET " +
                        "scoreReachedPoints = ${score.reachedPoints}, " +
                        "scoreTotalPoints = ${score.totalPoints}, " +
                        "scoreShotCount = ${score.shotCount} " +
                        "WHERE _id = $endId")
            }
        }
    }
}
