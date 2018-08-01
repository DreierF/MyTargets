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

import androidx.lifecycle.LiveData
import androidx.room.*
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.ArrowImage

@Dao
abstract class ArrowDAO {
    @Query("SELECT * FROM `Arrow`")
    abstract fun loadArrows(): List<Arrow>

    @Query("SELECT * FROM `Arrow`")
    abstract fun loadArrowsLive(): LiveData<List<Arrow>>

    @Query("SELECT * FROM `Arrow` WHERE `id` = (:id)")
    abstract fun loadArrow(id: Long): Arrow

    @Query("SELECT * FROM `Arrow` WHERE `id` = (:id)")
    abstract fun loadArrowOrNull(id: Long): Arrow?

    @Query("SELECT * FROM `ArrowImage` WHERE `arrowId` = (:id)")
    abstract fun loadArrowImages(id: Long): List<ArrowImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertArrow(arrow: Arrow): Long

    @Update
    abstract fun updateArrow(arrow: Arrow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertArrowImages(images: List<ArrowImage>)

    @Query("DELETE FROM `ArrowImage` WHERE `arrowId` = (:arrowId)")
    abstract fun deleteArrowImages(arrowId: Long)

    @Transaction
    open fun saveArrow(arrow: Arrow, images: List<ArrowImage>) {
        if (arrow.id > 0) {
            updateArrow(arrow)
        } else {
            arrow.id = insertArrow(arrow)
        }
        deleteArrowImages(arrow.id)
        for (image in images) {
            image.arrowId = arrow.id
        }
        insertArrowImages(images)
    }

    @Delete
    abstract fun deleteArrow(arrow: Arrow)
}
