/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.utils.multiselector

import android.util.LongSparseArray
import java.lang.ref.WeakReference

class WeakHolderTracker {
    private val holdersById = LongSparseArray<WeakReference<SelectableHolder>>()

    val trackedHolders: List<SelectableHolder>
        get() = (0 until holdersById.size())
                .map { holdersById.keyAt(it) }
                .mapNotNull { getHolder(it) }

    /**
     * Returns the holder with a given id.
     *
     * @param id
     * @return
     */
    fun getHolder(id: Long): SelectableHolder? {
        val holderRef = holdersById.get(id) ?: return null
        return holderRef.get()
    }

    fun bindHolder(holder: SelectableHolder, id: Long) {
        holdersById.put(id, WeakReference(holder))
    }
}
