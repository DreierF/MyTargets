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
import de.dreier.mytargets.shared.models.db.Signature

@Dao
interface SignatureDAO {

    @Query("SELECT * FROM `Signature` WHERE `id` = :id")
    fun loadSignature(id: Long): Signature

    @Query("SELECT * FROM `Signature` WHERE `id` = :id")
    fun loadSignatureOrNull(id: Long): Signature?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSignature(signature: Signature): Long

    @Update
    fun updateSignature(signature: Signature)

    @Delete
    fun deleteSignature(signature: Signature)
}
