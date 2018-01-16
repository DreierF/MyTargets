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

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.SharedApplicationInstance
import java.io.File
import java.io.IOException

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
