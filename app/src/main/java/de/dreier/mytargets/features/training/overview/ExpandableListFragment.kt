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
package de.dreier.mytargets.features.training.overview

import android.os.Bundle

import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.IRecursiveModel
import de.dreier.mytargets.shared.utils.LongUtils

/**
 * Shows all rounds of one training day
 */
abstract class ExpandableListFragment<H : IIdProvider, C> : EditableListFragmentBase<C, ExpandableListAdapter<H, C>>() where C : IIdSettable, C : IRecursiveModel {

    private var savedInstanceState: Bundle? = null

    protected fun setList(children: List<C>, opened: Boolean) {
        if (adapter!!.itemCount == 0) {
            adapter!!.setList(children, opened)
            if (savedInstanceState != null && savedInstanceState!!.containsKey(KEY_EXPANDED)) {
                adapter!!.setExpandedIds(
                        LongUtils.toList(savedInstanceState!!.getLongArray(KEY_EXPANDED)!!))
            } else if (!opened && adapter!!.itemCount > 0) {
                adapter!!.expandFirst()
            }
            return
        }
        adapter!!.setList(children)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (adapter != null) {
            outState.putLongArray(KEY_EXPANDED, LongUtils.toArray(adapter!!.expandedIds))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    companion object {
        private val KEY_EXPANDED = "expanded"
    }

}
