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

package de.dreier.mytargets.base.db.dao

import android.arch.persistence.room.*
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.BowImage
import de.dreier.mytargets.shared.models.db.SightMark

@Dao
abstract class BowDAO {
    @Query("SELECT * FROM Bow")
    abstract fun loadBows(): List<Bow>

    @Query("SELECT * FROM Bow WHERE _id = (:id)")
    abstract fun loadBow(id: Long): Bow

    @Query("SELECT * FROM Bow WHERE _id = (:id)")
    abstract fun loadBowOrNull(id: Long): Bow?

    @Query("SELECT * FROM BowImage WHERE bow = (:id)")
    abstract fun loadBowImages(id: Long): List<BowImage>

    @Query("SELECT * FROM SightMark WHERE bow = (:id)")
    abstract fun loadSightMarks(id: Long): List<SightMark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBow(bow: Bow): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBowImages(images: List<BowImage>)

    @Query("DELETE FROM BowImage WHERE bow = (:bowId)")
    abstract fun deleteBowImages(bowId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSightMarks(sightMarks: List<SightMark>)

    @Query("DELETE FROM SightMark WHERE bow = (:bowId)")
    abstract fun deleteSightMarks(bowId: Long)

    @Transaction
    open fun saveBow(bow: Bow, images: List<BowImage>, sightMarks: List<SightMark>) {
        bow.id = insertBow(bow)
        // deleteBowImages(bow.id) TODO test
        for (image in images) {
            image.bowId = bow.id
        }
        insertBowImages(images)
        // deleteSightMarks(bow.id) TODO
        for (sightMark in sightMarks) {
            sightMark.bowId = bow.id
        }
        insertSightMarks(sightMarks)
    }

    //    deleteSightMarks(bow.id) // TODO
    //    deleteBowImages(bow.id)
    @Delete
    abstract fun deleteBow(bow: Bow)
}
