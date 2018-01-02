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

/**
 *
 * A Selector that only allows for one position at a time to be selected.
 *
 * Any time [SelectorBase.setSelected] is called, all other selected positions are set to false.
 */
class SingleSelector : SelectorBase() {
    private var selectedId = -1L

    override fun setSelected(id: Long, isSelected: Boolean) {
        val oldId = selectedId
        selectedId = if (isSelected) {
            id
        } else {
            -1
        }
        refreshHolder(tracker.getHolder(oldId))
        refreshHolder(tracker.getHolder(selectedId))
    }

    public override fun isSelected(id: Long): Boolean {
        return id == selectedId && selectedId != -1L
    }

    override fun saveSelectionStates(bundle: Bundle) {
        bundle.putLong(SELECTION_ID, selectedId)
    }

    override fun restoreSelectionStates(savedStates: Bundle) {
        super.restoreSelectionStates(savedStates)
        selectedId = savedStates.getLong(SELECTION_ID)
    }

    fun getSelectedId(): Long? {
        return if (selectedId == -1L) null else selectedId
    }

    companion object {
        private const val SELECTION_ID = "selection_id"
    }
}
