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

package de.dreier.mytargets.utils

import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Shot

/**
 * Adds a new end to the internal list of ends, but does not yet save it.
 *
 * @return Returns the newly created end
 */
fun AugmentedRound.addEnd(): AugmentedEnd {
    val end = End(index = ends.size, roundId = round.id)
    val augmentedEnd = AugmentedEnd(end, (0 until round.shotsPerEnd)
        .map { Shot(it) }.toMutableList(), mutableListOf()
    )
    ApplicationInstance.db.endDAO()
        .insertCompleteEnd(augmentedEnd.end, augmentedEnd.images, augmentedEnd.shots)
    ends.add(augmentedEnd)
    return augmentedEnd
}
