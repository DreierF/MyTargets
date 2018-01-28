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
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.utils.moveTo
import java.io.File
import java.io.IOException

object Migration23 : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        removeFilePath(database, "EndImage")
        removeFilePath(database, "BowImage")
        removeFilePath(database, "ArrowImage")
    }

    private fun removeFilePath(database: SupportSQLiteDatabase, tableName: String) {
        val cursor = database.query("SELECT _id, fileName FROM $tableName")
        while (cursor.moveToNext()) {
            val imageId = cursor.getLong(0)
            val fileName = cursor.getString(1)
            val filesDir = SharedApplicationInstance.context.filesDir
            var imageFile = File(filesDir, fileName)

            val imageFromSomewhere = File(fileName)
            val imageFileFromSomewhere = File(filesDir, imageFromSomewhere.name)

            // If imagePath is just the name and is placed inside files directory or
            // In case the image was already copied to the files, but does still contain the wrong path
            if (imageFile.exists() || imageFileFromSomewhere.exists()) {
                database.execSQL("UPDATE $tableName SET fileName = ${imageFile.name} WHERE _id = $imageId")
                continue
            }

            // In case the image is placed somewhere else, but still exists
            if (imageFromSomewhere.exists()) {
                try {
                    imageFile = File.createTempFile("img", imageFromSomewhere.name, filesDir)
                    imageFromSomewhere.moveTo(imageFile)
                    database.execSQL("UPDATE $tableName SET fileName = ${imageFile.name} WHERE _id = $imageId")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                database.execSQL("DELETE FROM $tableName WHERE _id = $imageId")
            }
        }
    }
}
