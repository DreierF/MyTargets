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
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Image
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.shared.models.db.BowImage
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.utils.moveTo
import java.io.File
import java.io.IOException

@Migration(version = 23, database = AppDatabase::class)
class Migration23 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        removeFilePath(database, EndImage::class.java,
                { endImage, db -> endImage.save(db) },
                { endImage, db -> endImage.delete(db) })
        removeFilePath(database, BowImage::class.java,
                { bowImage, db -> bowImage.save(db) },
                { bowImage, db -> bowImage.delete(db) })
        removeFilePath(database, ArrowImage::class.java,
                { arrowImage, db -> arrowImage.save(db) },
                { arrowImage, db -> arrowImage.delete(db) })
    }

    private fun <T> removeFilePath(database: DatabaseWrapper, clazz: Class<out T>,
                                   save: (T, DatabaseWrapper) -> Boolean,
                                   delete: (T, DatabaseWrapper) -> Boolean) where T : Image {
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
                save(image, database)
                continue
            }

            // In case the image is placed somewhere else, but still exists
            if (imageFromSomewhere.exists()) {
                try {
                    imageFile = File.createTempFile("img", imageFromSomewhere.name, filesDir)
                    imageFromSomewhere.moveTo(imageFile)
                    image.fileName = imageFile.name
                    save(image, database)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                delete(image, database)
            }
        }
    }
}
