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

package de.dreier.mytargets.shared.models.dao

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.shared.models.db.ArrowImage_Table
import de.dreier.mytargets.shared.models.db.Arrow_Table

object ArrowDAO {
    fun loadArrows(): List<Arrow> = SQLite.select().from(Arrow::class.java).queryList()

    fun loadArrow(id: Long): Arrow = SQLite.select()
            .from(Arrow::class.java)
            .where(Arrow_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("Arrow $id does not exist")

    fun loadArrowOrNull(id: Long): Arrow? = SQLite.select()
            .from(Arrow::class.java)
            .where(Arrow_Table._id.eq(id))
            .querySingle()

    fun loadArrowImages(id: Long): List<ArrowImage> = SQLite.select()
            .from(ArrowImage::class.java)
            .where(ArrowImage_Table.arrow.eq(id))
            .queryList()

    fun saveArrow(arrow: Arrow, images: List<ArrowImage>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            arrow.save(db)
            SQLite.delete(ArrowImage::class.java)
                    .where(ArrowImage_Table.arrow.eq(arrow.id))
                    .execute(db)
            for (image in images) {
                image.arrowId = arrow.id
                image.save(db)
            }
        }
    }

    fun deleteArrow(arrow: Arrow) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
                    SQLite.delete(ArrowImage::class.java)
                            .where(ArrowImage_Table.arrow.eq(arrow.id))
                            .execute(db)
                    arrow.delete(db)
                }
    }
}
