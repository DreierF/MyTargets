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

import androidx.room.*
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.db.Shot

@Dao
abstract class EndDAO {

    @Query("SELECT * FROM `End` WHERE `roundId` = :roundId ORDER BY `index`")
    abstract fun loadEnds(roundId: Long): MutableList<End>

    @Query("SELECT * FROM `EndImage` WHERE `endId` = :id")
    abstract fun loadEndImages(id: Long): List<EndImage>

    @Query("SELECT * FROM `Shot` WHERE `endId` = :id ORDER BY `index`")
    abstract fun loadShots(id: Long): List<Shot>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertEnd(end: End): Long

    @Update
    abstract fun updateEnd(end: End)

    @Update
    abstract fun updateShots(shots: List<Shot>)

    @Transaction
    open fun insertEnd(end: End, images: List<EndImage>, shots: List<Shot>) {
        incrementIndices(end.index)
        insertCompleteEnd(end, images, shots)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertEndImage(images: EndImage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertShot(images: Shot): Long

    @Query("DELETE FROM `EndImage` WHERE `endId` = (:endId)")
    abstract fun deleteEndImages(endId: Long)

    @Transaction
    open fun insertCompleteEnd(end: End, images: List<EndImage>, shots: List<Shot>) {
        end.id = insertEnd(end)
        for (image in images) {
            image.endId = end.id
            image.id = insertEndImage(image)
        }

        for (shot in shots) {
            shot.endId = end.id
            shot.id = insertShot(shot)
        }
    }

    @Transaction
    open fun deleteEnd(end: End) {
        deleteEndWithoutIndexUpdate(end)
        decrementIndices(end.index)
    }

    @Delete
    abstract fun deleteEndWithoutIndexUpdate(end: End)

    @Query("UPDATE `End` SET `index` = `index` - 1 WHERE `index` > :allAboveIndex")
    abstract fun decrementIndices(allAboveIndex: Int)

    @Query("UPDATE `End` SET `index` = `index` + 1 WHERE `index` >= :allAboveIndex")
    abstract fun incrementIndices(allAboveIndex: Int)

    @Transaction
    open fun replaceImages(end: End, images: List<EndImage>) {
        deleteEndImages(end.id)
        for (image in images) {
            image.endId = end.id
            image.id = insertEndImage(image)
        }
    }
}
