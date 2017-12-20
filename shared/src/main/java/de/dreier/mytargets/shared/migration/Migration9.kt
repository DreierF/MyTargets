/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.shared.migration

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.utils.StandardRoundFactory
import java.util.*

@Migration(version = 9, database = AppDatabase::class)
class Migration9 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
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

        database.execSQL("CREATE TABLE IF NOT EXISTS " + SR_TABLE + " (" +
                SR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SR_NAME + " TEXT," +
                SR_INSTITUTION + " INTEGER," +
                SR_INDOOR + " INTEGER);")
        database.execSQL("CREATE TABLE IF NOT EXISTS " + RT_TABLE + " (" +
                RT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RT_STANDARD_ID + " INTEGER," +
                RT_INDEX + " INTEGER," +
                RT_DISTANCE + " INTEGER," +
                RT_UNIT + " TEXT," +
                RT_PASSES + " INTEGER," +
                RT_ARROWS_PER_PASSE + " INTEGER," +
                RT_TARGET + " INTEGER," +
                RT_TARGET_SIZE + " INTEGER," +
                RT_TARGET_SIZE_UNIT + " INTEGER," +
                RT_SCORING_STYLE + " INTEGER," +
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

        val trainings = database.rawQuery("SELECT _id FROM TRAINING", null)
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

                val res = database.rawQuery(
                        "SELECT r._id, r.comment, r.target, r.bow, r.arrow " +
                                "FROM ROUND_OLD r " +
                                "WHERE r.training=" + training + " " +
                                "GROUP BY r._id " +
                                "ORDER BY r._id ASC", null)
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
                        database.insertWithOnConflict("ROUND", null, contentValues, CONFLICT_IGNORE)
                        index++
                    } while (res.moveToNext())
                }
                res.close()
            } while (trainings.moveToNext())
        }
        trainings.close()
    }

    private fun getOrCreateStandardRound(db: DatabaseWrapper, training: Long): Long? {
        val res = db.rawQuery(
                "SELECT r.ppp, r.target, r.distance, r.unit, COUNT(p._id), r.indoor " +
                        "FROM ROUND_OLD r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY r._id " +
                        "ORDER BY r._id ASC", null)
        val sr = StandardRound()
        sr.name = "Practice"
        sr.club = 512
        sr.setRounds(ArrayList())
        if (res.moveToFirst()) {
            do {
                val template = RoundTemplate()
                template.shotsPerEnd = res.getInt(0)
                val target = res.getInt(1)
                template.targetTemplate = Target(
                        (if (target == 4) 5 else target).toLong(), if (target == 5) 1 else 0)
                template.distance = Dimension.from(res.getInt(2).toFloat(), res.getString(3))
                template.endCount = res.getInt(4)
                template.index = sr.loadRounds().size
                sr.loadRounds().add(template)
            } while (res.moveToNext())
        }
        res.close()

        if (sr.loadRounds().isEmpty()) {
            return 0L
        }
        insertStandardRound(db, sr)
        return sr.id
    }

    companion object {
        private val SR_ID = "_id"
        private val SR_TABLE = "STANDARD_ROUND_TEMPLATE"
        private val SR_NAME = "name"
        private val SR_INSTITUTION = "club"
        private val SR_INDOOR = "indoor"

        private val RT_ID = "_id"
        private val RT_TARGET = "target"
        private val RT_SCORING_STYLE = "scoring_style"
        private val RT_TABLE = "ROUND_TEMPLATE"
        private val RT_STANDARD_ID = "sid"
        private val RT_INDEX = "r_index"
        private val RT_DISTANCE = "distance"
        private val RT_UNIT = "unit"
        private val RT_PASSES = "passes"
        private val RT_ARROWS_PER_PASSE = "arrows"
        private val RT_TARGET_SIZE = "size"
        private val RT_TARGET_SIZE_UNIT = "target_unit"

        private fun insertStandardRound(database: DatabaseWrapper, item: StandardRound) {
            val values = ContentValues()
            values.put(SR_NAME, item.name)
            values.put(SR_INSTITUTION, item.club)
            values.put(SR_INDOOR, 0)
            if (item.id == null) {
                item.id = database
                        .insertWithOnConflict(SR_TABLE, null, values, SQLiteDatabase.CONFLICT_NONE)
                for (r in item.loadRounds()) {
                    r.standardRound = item.id
                }
            } else {
                values.put(SR_ID, item.id)
                database.insertWithOnConflict(SR_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            for (template in item.loadRounds()) {
                template.standardRound = item.id
                insertRoundTemplate(database, template)
            }
        }

        private fun insertRoundTemplate(database: DatabaseWrapper, item: RoundTemplate) {
            val values = ContentValues()
            values.put(RT_STANDARD_ID, item.standardRound)
            values.put(RT_INDEX, item.index)
            values.put(RT_DISTANCE, item.distance.value)
            values.put(RT_UNIT, Dimension.Unit.toStringHandleNull(item.distance.unit))
            values.put(RT_PASSES, item.endCount)
            values.put(RT_ARROWS_PER_PASSE, item.shotsPerEnd)
            values.put(RT_TARGET, (item.targetTemplate.id as Long).toInt())
            values.put(RT_TARGET_SIZE, item.targetTemplate.diameter!!.value)
            values.put(RT_TARGET_SIZE_UNIT, Dimension.Unit
                    .toStringHandleNull(item.targetTemplate.diameter!!.unit))
            values.put(RT_SCORING_STYLE, item.targetTemplate.scoringStyleIndex)
            if (item.id == null) {
                item.id = database
                        .insertWithOnConflict(RT_TABLE, null, values, SQLiteDatabase.CONFLICT_NONE)
            } else {
                values.put(RT_ID, item.id)
                database.insertWithOnConflict(RT_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
        }

        private fun getRoundTemplate(database: DatabaseWrapper, sid: Long, index: Int): RoundTemplate? {
            val cursor = database.rawQuery("SELECT _id, r_index, arrows, target, scoring_style, " +
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
            roundTemplate.standardRound = cursor.getLong(startColumnIndex + 12)
            return roundTemplate
        }
    }
}
