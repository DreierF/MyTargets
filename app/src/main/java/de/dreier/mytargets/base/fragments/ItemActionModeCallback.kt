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

package de.dreier.mytargets.base.fragments

import android.support.annotation.PluralsRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import de.dreier.mytargets.R
import de.dreier.mytargets.utils.multiselector.MultiSelector
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder


typealias EditCallback = (Long) -> Unit

typealias DeleteCallback = (List<Long>) -> Unit

typealias StatisticsCallback = (List<Long>) -> Unit

class ItemActionModeCallback(
    private val fragment: FragmentBase,
    private val selector: MultiSelector,
    @PluralsRes private val itemTitleRes: Int
) : ActionMode.Callback {

    private var actionMode: ActionMode? = null

    /**
     * Callbacks for edit, delete and statistics
     * Null values indicate that the operation is not supported for the presented item type.
     */
    private var editCallback: EditCallback? = null
    private var deleteCallback: DeleteCallback? = null
    private var statisticsCallback: StatisticsCallback? = null

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val edit = menu.findItem(R.id.action_edit)
        edit.isVisible = selector.selectedItemCount == 1
        menu.findItem(R.id.action_statistics).isVisible = statisticsCallback != null
        menu.findItem(R.id.action_delete).isVisible = deleteCallback != null
        return false
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        selector.selectable = true
        actionMode = mode
        mode.menuInflater.inflate(R.menu.context_menu_edit_delete, menu)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val ids = selector.selectedIds
        when (item.itemId) {
            R.id.action_edit -> {
                editCallback?.invoke(ids[0])
                mode.finish()
                return true
            }
            R.id.action_statistics -> {
                statisticsCallback?.invoke(ids)
                mode.finish()
                return true
            }
            R.id.action_delete -> {
                deleteCallback?.invoke(ids)
                mode.finish()
                return true
            }
            else -> return false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        selector.selectable = false
        selector.clearSelections()
        actionMode = null
    }

    fun longClick(holder: SelectableViewHolder<*>) {
        if (actionMode == null) {
            val activity = fragment.activity as AppCompatActivity?
            activity!!.startSupportActionMode(this)
        }
        selector.setSelected(holder, true)
        updateTitle()
    }

    fun restartActionMode() {
        val activity = fragment.getActivity() as AppCompatActivity?
        activity!!.startSupportActionMode(this)
        updateTitle()
    }

    /**
     * Returns true if the click has been handled.
     */
    fun click(holder: SelectableViewHolder<*>): Boolean {
        if (selector.tapSelection(holder)) {
            updateTitle()
            return true
        }
        return false
    }

    private fun updateTitle() {
        if (actionMode == null) {
            return
        }
        val count = selector.selectedItemCount
        if (count == 0) {
            finish()
        } else {
            actionMode!!.title =
                    fragment.getResources().getQuantityString(itemTitleRes, count, count)
            actionMode!!.invalidate()
        }
    }

    fun setEditCallback(editCallback: EditCallback?) {
        this.editCallback = editCallback
    }

    fun setDeleteCallback(deleteCallback: DeleteCallback?) {
        this.deleteCallback = deleteCallback
    }

    fun setStatisticsCallback(statisticsCallback: StatisticsCallback?) {
        this.statisticsCallback = statisticsCallback
    }

    fun finish() {
        actionMode?.finish()
    }
}
