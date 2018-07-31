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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.arrows.ArrowListViewModel
import de.dreier.mytargets.features.arrows.EditArrowViewModel
import de.dreier.mytargets.features.distance.DistancesViewModel
import de.dreier.mytargets.features.training.details.TrainingViewModel
import de.dreier.mytargets.features.training.overview.TrainingsViewModel

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            EditArrowViewModel::class.java -> EditArrowViewModel(ApplicationInstance.instance) as T
            TrainingViewModel::class.java -> TrainingViewModel(ApplicationInstance.instance) as T
            TrainingsViewModel::class.java -> TrainingsViewModel(ApplicationInstance.instance) as T
            ArrowListViewModel::class.java -> ArrowListViewModel(ApplicationInstance.instance) as T
            DistancesViewModel::class.java -> DistancesViewModel(ApplicationInstance.instance) as T
            else -> throw Exception("No implementation for $modelClass provided")
        }
    }
}
