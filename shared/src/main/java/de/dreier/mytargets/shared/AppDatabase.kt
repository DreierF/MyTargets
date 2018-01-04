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

package de.dreier.mytargets.shared

import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.shared.models.db.BowImage
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.db.Image
import de.dreier.mytargets.shared.utils.StandardRoundFactory
import de.dreier.mytargets.shared.utils.moveTo
import java.io.File
import java.io.IOException

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeyConstraintsEnforced = true)
object AppDatabase {

    const val NAME = "database"
    const val DATABASE_FILE_NAME = "database.db"
    const val DATABASE_IMPORT_FILE_NAME = "database"
    const val VERSION = 25

    @Migration(version = 0, database = AppDatabase::class)
    class Migration0 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            fillStandardRound(database)
        }
    }

    @Migration(version = 22, database = AppDatabase::class)
    class Migration22 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            fillStandardRound(database)
        }
    }

    @Migration(version = 23, database = AppDatabase::class)
    class Migration23 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            removeFilePath(database, EndImage::class.java)
            removeFilePath(database, BowImage::class.java)
            removeFilePath(database, ArrowImage::class.java)
        }

        private fun <T> removeFilePath(database: DatabaseWrapper, clazz: Class<out T>) where T : BaseModel, T : Image {
            val images = SQLite.select().from(clazz).queryList(database)
            for (image in images) {
                val filesDir = SharedApplicationInstance.context.filesDir
                var imageFile = File(filesDir, image.fileName)

                val imageFromSomewhere = File(image.fileName)
                val imageFileFromSomewhere = File(filesDir, imageFromSomewhere.name)

                // If imagePath is just the name and is placed inside files directory or
                // In case the image was already copied to the files, but does still contain the wrong path
                if (imageFile.exists() || imageFileFromSomewhere.exists()) {
                    image.fileName = imageFile.name
                    image.save(database)
                    continue
                }

                // In case the image is placed somewhere else, but still exists
                if (imageFromSomewhere.exists()) {
                    try {
                        imageFile = File
                                .createTempFile("img", imageFromSomewhere.name, filesDir)
                        imageFromSomewhere.moveTo(imageFile)
                        image.fileName = imageFile.name
                        image.save(database)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    image.delete(database)
                }
            }
        }
    }

    private fun fillStandardRound(db: DatabaseWrapper) {
        val rounds = StandardRoundFactory.initTable()
        for (round in rounds) {
            round.save(db)
        }
    }

    @Migration(version = 3, database = AppDatabase::class)
    class Migration3 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN x REAL")
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN y REAL")
            val cur = database.rawQuery("SELECT s._id, s.points, r.target " +
                    "FROM SHOOT s, PASSE p, ROUND r " +
                    "WHERE s.passe=p._id " +
                    "AND p.round=r._id", null)
            if (cur.moveToFirst()) {
                do {
                    val shoot = cur.getInt(0)
                    database.execSQL("UPDATE SHOOT SET x=0, y=0 WHERE _id=" + shoot)
                } while (cur.moveToNext())
            }
            cur.close()
        }
    }

    @Migration(version = 4, database = AppDatabase::class)
    class Migration4 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS VISIER ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "bow REFERENCES BOW ON DELETE CASCADE," +
                            "distance INTEGER," +
                            "setting TEXT);")
            val valuesMetric = intArrayOf(10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90)
            for (table in arrayOf("ROUND", "VISIER")) {
                for (i in 10 downTo 0) {
                    database.execSQL("UPDATE " + table + " SET distance=" +
                            valuesMetric[i] + " WHERE distance=" + i)
                }
            }
            database.execSQL("ALTER TABLE BOW ADD COLUMN height TEXT DEFAULT '';")
        }
    }

    @Migration(version = 6, database = AppDatabase::class)
    class Migration6 : BaseMigration() {

        override fun migrate(database: DatabaseWrapper) {
            val filesDir = SharedApplicationInstance.context.filesDir

            // Migrate all bow images
            var cur = database.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null)
            if (cur.moveToFirst()) {
                val fileName = cur.getString(0)
                try {
                    val file = File.createTempFile("img_", ".png", filesDir)
                    File(fileName).copyTo(file)
                    database.execSQL(
                            "UPDATE BOW SET image=\"" + file.name + "\" WHERE image=\"" +
                                    fileName + "\"")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            cur.close()

            // Migrate all arrows images
            cur = database.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null)
            if (cur.moveToFirst()) {
                val fileName = cur.getString(0)
                try {
                    val file = File.createTempFile("img_", ".png", filesDir)
                    File(fileName).copyTo(file)
                    database.execSQL(
                            "UPDATE ARROW SET image=\"" + file.name + "\" WHERE image=\"" +
                                    fileName + "\"")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            cur.close()
        }
    }
}
