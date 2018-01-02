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

import android.os.Bundle
import java.util.*

class MultiSelector : SelectorBase() {
    private val selections = HashSet<Long>()

    val selectedIds: ArrayList<Long>
        get() = ArrayList(selections)

    override fun setSelected(id: Long, isSelected: Boolean) {
        if (isSelected) {
            selections.add(id)
        } else {
            selections.remove(id)
        }
        refreshHolder(tracker.getHolder(id))
    }

    override fun isSelected(id: Long): Boolean {
        return selections.contains(id)
    }

    fun clearSelections() {
        selections.clear()
        refreshAllHolders()
    }

    override fun saveSelectionStates(bundle: Bundle) {
        bundle.putLongArray(SELECTION_IDS, selectedIds.toLongArray())
    }

    override fun restoreSelectionStates(savedStates: Bundle) {
        super.restoreSelectionStates(savedStates)
        val selectedIds = savedStates.getLongArray(SELECTION_IDS)
        restoreSelections(selectedIds!!.toList())
    }

    private fun restoreSelections(selected: List<Long>?) {
        if (selected != null) {
            selections.clear()
            selections.addAll(selected)
            refreshAllHolders()
        }
    }

    companion object {
        private const val SELECTION_IDS = "ids"
    }
}
