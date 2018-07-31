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

package de.dreier.mytargets.base.db

import de.dreier.mytargets.base.db.dao.EndDAO
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd

class EndRepository(
    private val endDAO: EndDAO
) {
    fun loadAugmentedEnds(roundId: Long): List<AugmentedEnd> {
        return endDAO.loadEnds(roundId)
            .map {
                AugmentedEnd(
                    it,
                    endDAO.loadShots(it.id).toMutableList(),
                    endDAO.loadEndImages(it.id).toMutableList()
                )
            }
            .toMutableList()
    }
}
