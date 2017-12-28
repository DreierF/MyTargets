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
import android.support.annotation.CallSuper

abstract class SelectorBase {
    protected var tracker = WeakHolderTracker()
    private var isSelectable: Boolean = false

    fun setSelectable(isSelectable: Boolean) {
        this.isSelectable = isSelectable
        refreshAllHolders()
    }

    protected fun refreshAllHolders() {
        for (holder in tracker.trackedHolders) {
            refreshHolder(holder)
        }
    }

    protected fun refreshHolder(holder: SelectableHolder?) {
        if (holder != null) {
            if (holder is ItemBindingHolder<*> && holder.item != null) {
                holder.bindItem()
            }
            holder.isSelectable = isSelectable
            val isActivated = isSelected(holder.itemIdentifier)
            holder.isActivated = isActivated
        }
    }

    fun setSelected(holder: SelectableHolder, isSelected: Boolean) {
        setSelected(holder.itemIdentifier, isSelected)
    }

    abstract fun setSelected(id: Long, isSelected: Boolean)

    protected abstract fun isSelected(id: Long): Boolean

    fun tapSelection(holder: SelectableHolder): Boolean {
        val itemId = holder.itemIdentifier
        return if (isSelectable) {
            val isSelected = isSelected(itemId)
            setSelected(itemId, !isSelected)
            true
        } else {
            false
        }
    }

    fun saveSelectionStates(): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(SELECTIONS_STATE, isSelectable)
        saveSelectionStates(bundle)
        return bundle
    }

    fun bindHolder(holder: SelectableHolder, id: Long) {
        tracker.bindHolder(holder, id)
        refreshHolder(holder)
    }

    protected abstract fun saveSelectionStates(bundle: Bundle)

    @CallSuper
    open fun restoreSelectionStates(savedStates: Bundle) {
        isSelectable = savedStates.getBoolean(SELECTIONS_STATE)
    }

    companion object {
        private const val SELECTIONS_STATE = "state"
    }
}
