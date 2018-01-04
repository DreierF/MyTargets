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

package de.dreier.mytargets.shared.models.dao

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.db.*

object BowDAO {
    fun loadBows(): List<Bow> = SQLite.select().from(Bow::class.java).queryList()

    fun loadBow(id: Long): Bow = SQLite.select()
            .from(Bow::class.java)
            .where(Bow_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("Bow $id does not exist")

    fun loadBowOrNull(id: Long): Bow? = SQLite.select()
            .from(Bow::class.java)
            .where(Bow_Table._id.eq(id))
            .querySingle()

    fun loadBowImages(id: Long): List<BowImage> = SQLite.select()
            .from(BowImage::class.java)
            .where(BowImage_Table.bow.eq(id))
            .queryList()

    fun loadSightMarks(id: Long): ArrayList<SightMark> = ArrayList(SQLite.select()
            .from(SightMark::class.java)
            .where(SightMark_Table.bow.eq(id))
            .queryList()
            .sortedBy { sightMark -> sightMark.distance }
            .toMutableList())

    fun saveBow(bow: Bow, images: List<BowImage>, sightMarks: List<SightMark>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
                    bow.save(db)
                    SQLite.delete(BowImage::class.java)
                            .where(BowImage_Table.bow.eq(bow.id))
                            .execute(db)
                    for (image in images) {
                        image.bowId = bow.id
                        image.save(db)
                    }
                    SQLite.delete(SightMark::class.java)
                            .where(SightMark_Table.bow.eq(bow.id))
                            .execute(db)
                    for (sightMark in sightMarks) {
                        sightMark.bowId = bow.id
                        sightMark.save(db)
                    }
                }
    }

    fun deleteBow(bow: Bow) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
                    SQLite.delete(BowImage::class.java)
                            .where(BowImage_Table.bow.eq(bow.id))
                            .execute(db)
                    SQLite.delete(SightMark::class.java)
                            .where(SightMark_Table.bow.eq(bow.id))
                            .execute(db)
                    bow.delete(db)
                }
    }
}
