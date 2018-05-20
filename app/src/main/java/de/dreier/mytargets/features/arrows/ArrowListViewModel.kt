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

package de.dreier.mytargets.features.arrows

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.LiveDataUtil2

class ArrowListViewModel(app: ApplicationInstance) : AndroidViewModel(app) {

    private val arrowDAO = ApplicationInstance.db.arrowDAO()
    val arrows  = arrowDAO.loadArrowsLive()

    fun deleteArrow(item: Arrow): () -> Arrow {
        val images = arrowDAO.loadArrowImages(item.id)
        arrowDAO.deleteArrow(item)
        return {
            arrowDAO.saveArrow(item, images)
            item
        }
    }

}
