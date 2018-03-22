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
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.*
import de.dreier.mytargets.base.db.StandardRoundFactory
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound

object Migration9 : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS ZONE_MATRIX")
        database.execSQL("ALTER TABLE VISIER ADD COLUMN unit TEXT DEFAULT 'm'")
        database.execSQL("ALTER TABLE PASSE ADD COLUMN image TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow INTEGER DEFAULT '-1'")
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow_index INTEGER DEFAULT '-1'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN weather INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_speed INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_direction INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN location TEXT DEFAULT ''")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN standard_round INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN bow INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow_numbering INTEGER DEFAULT '0'")
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN time INTEGER DEFAULT '-1'")
        database.execSQL("UPDATE ROUND SET target=11 WHERE target=6") // DFBV Spiegel Spot
        database.execSQL("UPDATE ROUND SET target=10 WHERE target=5") // DFBV Spiegel
        database.execSQL("UPDATE ROUND SET target=13 WHERE target=4") // WA Field
        database.execSQL("UPDATE ROUND SET target=4 WHERE target=3") // WA 3 Spot -> vegas

        // Set all compound 3 spot to vertical
        database.execSQL("UPDATE ROUND SET target=target+1 " +
                "WHERE _id IN (SELECT r._id FROM ROUND r " +
                "LEFT JOIN BOW b ON b._id=r.bow " +
                "WHERE (r.bow=-2 OR b.type=1) AND r.target=4)")

        // Add shot indices
        database.execSQL("UPDATE SHOOT SET arrow_index=( " +
                "SELECT COUNT(*) FROM SHOOT s " +
                "WHERE s._id<SHOOT._id " +
                "AND s.passe=SHOOT.passe) " +
                "WHERE arrow_index=-1")

        // transform before inner points to after inner points
        database.execSQL("UPDATE SHOOT SET x = x/2.0, y = y/2.0 " +
                "WHERE _id IN (SELECT s._id " +
                "FROM ROUND r " +
                "LEFT JOIN PASSE p ON r._id = p.round " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "WHERE r.target<11 AND s.points=0);")
        database.execSQL("ALTER TABLE ROUND RENAME TO ROUND_OLD")

        database.execSQL("CREATE TABLE IF NOT EXISTS STANDARD_ROUND_TEMPLATE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "club INTEGER," +
                "indoor INTEGER);")
        database.execSQL("CREATE TABLE IF NOT EXISTS ROUND_TEMPLATE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sid INTEGER," +
                "r_index INTEGER," +
                "distance INTEGER," +
                "unit TEXT," +
                "passes INTEGER," +
                "arrows INTEGER," +
                "target INTEGER," +
                "size INTEGER," +
                "target_unit INTEGER," +
                "scoring_style INTEGER," +
                "UNIQUE(sid, r_index) ON CONFLICT REPLACE);")
        val rounds = StandardRoundFactory.initTable()
        for (round in rounds) {
            insertStandardRound(database, round)
        }

        database.execSQL("CREATE TABLE IF NOT EXISTS ROUND (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "training INTEGER," +
                "comment TEXT," +
                "template INTEGER," +
                "target INTEGER," +
                "scoring_style INTEGER);")

        val trainings = database.query("SELECT _id FROM TRAINING")
        if (trainings.moveToFirst()) {
            do {
                val training = trainings.getLong(0)
                val sid = getOrCreateStandardRound(database, training)!!
                if (sid == 0L) {
                    database.execSQL("DELETE FROM TRAINING WHERE _id=" + training)
                } else {
                    database.execSQL(
                            "UPDATE TRAINING SET standard_round=$sid WHERE _id=$training")
                }

                val res = database.query(
                        "SELECT r._id, r.comment, r.target, r.bow, r.arrow " +
                                "FROM ROUND_OLD r " +
                                "WHERE r.training=" + training + " " +
                                "GROUP BY r._id " +
                                "ORDER BY r._id ASC")
                var index = 0
                if (res.moveToFirst()) {
                    val bow = res.getLong(3)
                    val arrow = res.getLong(4)
                    database.execSQL(
                            "UPDATE TRAINING SET bow=" + bow + ", arrow=" + arrow + " WHERE _id=" +
                                    training)
                    do {
                        val info = getRoundTemplate(database, sid, index)
                        val target = res.getInt(2)
                        val contentValues = ContentValues()
                        contentValues.put("_id", res.getLong(0))
                        contentValues.put("comment", res.getString(1))
                        contentValues.put("training", training)
                        contentValues.put("template", info!!.id)
                        contentValues
                                .put("target", if (target == 4) 5 else target)
                        contentValues.put("scoring_style",
                                if (target == 5) 1 else 0)
                        database.insert("ROUND", CONFLICT_IGNORE, contentValues)
                        index++
                    } while (res.moveToNext())
                }
                res.close()
            } while (trainings.moveToNext())
        }
        trainings.close()
    }

    private fun getOrCreateStandardRound(db: SupportSQLiteDatabase, training: Long): Long? {
        val res = db.query(
                "SELECT r.ppp, r.target, r.distance, r.unit, COUNT(p._id), r.indoor " +
                        "FROM ROUND_OLD r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY r._id " +
                        "ORDER BY r._id ASC")
        val sr = StandardRound()
        sr.name = "Practice"
        sr.club = 512
        val rounds = mutableListOf<RoundTemplate>()
        if (res.moveToFirst()) {
            do {
                val template = RoundTemplate()
                template.shotsPerEnd = res.getInt(0)
                val target = res.getInt(1)
                template.targetTemplate = Target(
                        (if (target == 4) 5 else target).toLong(), if (target == 5) 1 else 0)
                template.distance = Dimension.from(res.getInt(2).toFloat(), res.getString(3))
                template.endCount = res.getInt(4)
                template.index = rounds.size
                rounds.add(template)
            } while (res.moveToNext())
        }
        res.close()

        if (loadRoundTemplates(db, sr.id).isEmpty()) {
            return 0L
        }
        insertStandardRound(db, AugmentedStandardRound(sr, rounds))
        return sr.id
    }

    private fun insertStandardRound(database: SupportSQLiteDatabase, item: AugmentedStandardRound) {
        val values = ContentValues()
        values.put("name", item.standardRound.name)
        values.put("club", item.standardRound.club)
        values.put("indoor", 0)
        if (item.id == 0L) {
            item.standardRound.id = database
                    .insert("STANDARD_ROUND_TEMPLATE", CONFLICT_NONE, values)
            for (r in item.roundTemplates) {
                r.standardRoundId = item.id
            }
        } else {
            values.put("_id", item.id)
            database.insert("STANDARD_ROUND_TEMPLATE", CONFLICT_REPLACE, values)
        }
        for (template in item.roundTemplates) {
            template.standardRoundId = item.id
            insertRoundTemplate(database, template)
        }
    }

    private fun insertRoundTemplate(database: SupportSQLiteDatabase, item: RoundTemplate) {
        val values = ContentValues()
        values.put("sid", item.standardRoundId)
        values.put("r_index", item.index)
        values.put("distance", item.distance.value)
        values.put("unit", item.distance.unit?.toString())
        values.put("passes", item.endCount)
        values.put("arrows", item.shotsPerEnd)
        values.put("target", item.targetTemplate.id.toInt())
        values.put("size", item.targetTemplate.diameter.value)
        values.put("target_unit", item.targetTemplate.diameter.unit?.toString())
        values.put("scoring_style", item.targetTemplate.scoringStyleIndex)
        if (item.id == 0L) {
            item.id = database
                    .insert("ROUND_TEMPLATE", CONFLICT_NONE, values)
        } else {
            values.put("_id", item.id)
            database.insert("ROUND_TEMPLATE", CONFLICT_REPLACE, values)
        }
    }

    private fun loadRoundTemplates(database: SupportSQLiteDatabase, sid: Long): List<RoundTemplate> {
        val cursor = database.query("SELECT _id, r_index, arrows, target, scoring_style, " +
                "target, scoring_style, distance, unit, size, target_unit, passes, sid " +
                "FROM ROUND_TEMPLATE WHERE sid=?",
                arrayOf(sid.toString()))
        val r: MutableList<RoundTemplate> = mutableListOf()
        if (cursor.moveToFirst()) {
            r.add(cursorToRoundTemplate(cursor, 0))
        }
        cursor.close()
        return r
    }

    private fun getRoundTemplate(database: SupportSQLiteDatabase, sid: Long, index: Int): RoundTemplate? {
        val cursor = database.query("SELECT _id, r_index, arrows, target, scoring_style, " +
                "target, scoring_style, distance, unit, size, target_unit, passes, sid " +
                "FROM ROUND_TEMPLATE WHERE sid=? AND r_index=?",
                arrayOf(sid.toString(), index.toString()))
        var r: RoundTemplate? = null
        if (cursor.moveToFirst()) {
            r = cursorToRoundTemplate(cursor, 0)
        }
        cursor.close()
        return r
    }

    private fun cursorToRoundTemplate(cursor: Cursor, startColumnIndex: Int): RoundTemplate {
        val roundTemplate = RoundTemplate()
        roundTemplate.id = cursor.getLong(startColumnIndex)
        roundTemplate.index = cursor.getInt(startColumnIndex + 1)
        roundTemplate.shotsPerEnd = cursor.getInt(startColumnIndex + 2)
        val diameter = Dimension.from(
                cursor.getInt(startColumnIndex + 9).toFloat(), cursor.getString(startColumnIndex + 10))
        roundTemplate.targetTemplate = Target(cursor.getLong(startColumnIndex + 3),
                cursor.getInt(startColumnIndex + 4), diameter)
        roundTemplate.distance = Dimension.from(cursor.getInt(startColumnIndex + 7).toFloat(),
                cursor.getString(startColumnIndex + 8))
        roundTemplate.endCount = cursor.getInt(startColumnIndex + 11)
        roundTemplate.standardRoundId = cursor.getLong(startColumnIndex + 12)
        return roundTemplate
    }
}
