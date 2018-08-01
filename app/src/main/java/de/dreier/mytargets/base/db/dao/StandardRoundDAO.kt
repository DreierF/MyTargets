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
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound

@Dao
abstract class StandardRoundDAO {
    @Query("SELECT * FROM `StandardRound`")
    abstract fun loadStandardRounds(): List<StandardRound>

    @Query("SELECT * FROM `StandardRound` WHERE `id` = :id")
    abstract fun loadStandardRound(id: Long): StandardRound

    @Transaction
    open fun loadAugmentedStandardRound(id: Long): AugmentedStandardRound =
        AugmentedStandardRound(loadStandardRound(id), loadRoundTemplates(id).toMutableList())

    @Query("SELECT * FROM `StandardRound` WHERE `id` = :id")
    abstract fun loadStandardRoundOrNull(id: Long): StandardRound?

    @Query("SELECT * FROM `StandardRound` WHERE `name` LIKE :query AND `club` != 512")
    abstract fun getAllSearch(query: String): List<StandardRound>

    @Query("SELECT * FROM `RoundTemplate` WHERE `standardRoundId` = :id ORDER BY `index`")
    abstract fun loadRoundTemplates(id: Long): List<RoundTemplate>

    @Insert
    abstract fun insertStandardRound(round: StandardRound): Long

    @Update
    abstract fun updateStandardRound(round: StandardRound)

    @Insert
    abstract fun insertRoundTemplate(round: RoundTemplate): Long

    @Query("DELETE FROM `RoundTemplate` WHERE `standardRoundId` = (:id)")
    abstract fun deleteRoundTemplates(id: Long)

    @Transaction
    open fun saveStandardRound(standardRound: StandardRound, roundTemplates: List<RoundTemplate>) {
        if(standardRound.id == 0L) {
            standardRound.id = insertStandardRound(standardRound)
        } else {
            updateStandardRound(standardRound)
        }
        deleteRoundTemplates(standardRound.id)
        for (roundTemplate in roundTemplates) {
            roundTemplate.standardRoundId = standardRound.id
            roundTemplate.id = insertRoundTemplate(roundTemplate)
        }
    }

    @Delete
    abstract fun deleteStandardRound(standardRound: StandardRound)
}
