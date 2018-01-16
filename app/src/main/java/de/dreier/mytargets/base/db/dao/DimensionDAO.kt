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

import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.db.*
import java.util.*

object DimensionDAO {
    /**
     * Returns a list of all distances that are either default values or used somewhere in the app
     *
     * @param distance Distance to add to the list (current selected value)
     * @param unit     Distances are only returned which match the specified unit
     * @return List of distances
     */
    fun getAll(distance: Dimension, unit: Dimension.Unit): List<Dimension> {
        val distances = HashSet<Dimension>()

        distances.add(Dimension.UNKNOWN)

        // Add currently selected distance to list
        if (distance.unit == unit) {
            distances.add(distance)
        }

        // Get all distances used in Round or SightMark table
        distances.addAll(SQLite
                .select(SightMark_Table.distance)
                .from(SightMark::class.java)
                .queryList()
                .map { it.distance }
                .filter { it.unit == unit }
                .toSet())

        distances.addAll(SQLite
                .select(RoundTemplate_Table.distance)
                .from(RoundTemplate::class.java)
                .queryList()
                .map { it.distance }
                .filter { it.unit == unit }
                .toSet())

        distances.addAll(SQLite
                .select(Round_Table.distance)
                .from(Round::class.java)
                .queryList()
                .map { it.distance }
                .filter { it.unit == unit }
                .toSet())

        return ArrayList(distances)
    }
}
