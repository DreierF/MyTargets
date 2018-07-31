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

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import de.dreier.mytargets.shared.models.Dimension

@Dao
interface DimensionDAO {
    /**
     * Returns a list of all distances that are either default values or used somewhere in the app
     *
     * @param unit     Distances are only returned which match the specified unit
     * @return List of distances
     */
    @Query(
        "SELECT distance FROM `SightMark` WHERE `distance` LIKE ('% ' || :unit)" +
                "UNION SELECT distance FROM `RoundTemplate` WHERE `distance` LIKE ('% ' || :unit)" +
                "UNION SELECT distance FROM `Round` WHERE `distance` LIKE ('% ' || :unit)"
    )
    fun getAll(unit: Dimension.Unit): LiveData<List<Dimension>>
}
