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

package de.dreier.mytargets.features.distance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.models.Dimension

class DistancesViewModel(app: Application) : AndroidViewModel(app) {

    private val unit = MutableLiveData<Dimension.Unit>()
    private var distance = MutableLiveData<Dimension?>()

    private val dimensionDAO = ApplicationInstance.db.dimensionDAO()
    val distances: LiveData<List<Dimension>>

    init {
        val dbDistances = Transformations.switchMap(unit) { unit ->
            dimensionDAO.getAll(unit)
        }
        distances = Transformations.map(dbDistances) { filteredDistances ->
            val distances = mutableSetOf(de.dreier.mytargets.shared.models.Dimension.UNKNOWN)

            // Add currently selected distance to list
            if (this.distance.value?.unit == unit) {
                distances.add(this.distance.value!!)
            }
            distances.addAll(filteredDistances)
            distances.toList()
        }
    }

    fun setUnit(unit: Dimension.Unit) {
        this.unit.value = unit
    }

    fun setDistance(distance: Dimension?) {
        this.distance.value = distance
    }

    fun createDistanceFromInput(input: String): Dimension {
        return try {
            val distanceVal = input.replace("[^0-9]".toRegex(), "").toInt()
            Dimension(distanceVal.toFloat(), unit.value)
        } catch (e: NumberFormatException) {
            // leave distance as it is
            this.distance.value ?: Dimension(10f, unit.value!!)
        }
    }
}
