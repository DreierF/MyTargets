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

package de.dreier.mytargets.base.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.dreier.mytargets.features.arrows.ArrowListViewModel
import de.dreier.mytargets.features.arrows.EditArrowViewModel
import de.dreier.mytargets.features.distance.DistancesViewModel
import de.dreier.mytargets.features.training.details.TrainingViewModel
import de.dreier.mytargets.features.training.overview.TrainingsViewModel

internal class ViewModelFactory(private val applicationInstance: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            EditArrowViewModel::class.java -> EditArrowViewModel(applicationInstance) as T
            TrainingViewModel::class.java -> TrainingViewModel(applicationInstance) as T
            TrainingsViewModel::class.java -> TrainingsViewModel(applicationInstance) as T
            ArrowListViewModel::class.java -> ArrowListViewModel(applicationInstance) as T
            DistancesViewModel::class.java -> DistancesViewModel(applicationInstance) as T
            else -> throw Exception("No implementation for $modelClass provided")
        }
    }
}
