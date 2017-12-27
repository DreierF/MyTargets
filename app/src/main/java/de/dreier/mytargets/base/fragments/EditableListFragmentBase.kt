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
package de.dreier.mytargets.base.fragments

import android.support.annotation.PluralsRes
import android.support.design.widget.Snackbar
import android.view.View
import com.evernote.android.state.State
import com.google.firebase.analytics.FirebaseAnalytics
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.ListAdapterBase
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.IRecursiveModel
import de.dreier.mytargets.utils.MultiSelectorBundler
import de.dreier.mytargets.utils.multiselector.MultiSelector
import de.dreier.mytargets.utils.multiselector.OnItemLongClickListener
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

/**
 * @param <T> Model of the item which is managed within the fragment.
</T> */
abstract class EditableListFragmentBase<T, U : ListAdapterBase<*, T>> : ListFragmentBase<T, U>(), OnItemLongClickListener<T> where T : IIdSettable, T : IRecursiveModel {

    @State(MultiSelectorBundler::class)
    var selector = MultiSelector()

    /**
     * Resource used to set title when items are deleted.
     */
    @PluralsRes
    protected var itemTypeDelRes: Int = 0

    var actionModeCallback: ItemActionModeCallback? = null

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    fun onDelete(deletedIds: List<Long>) {
        FirebaseAnalytics.getInstance(context!!).logEvent("delete", null)
        val deleted = deleteItems(deletedIds)
        val message = resources
                .getQuantityString(itemTypeDelRes, deleted.size, deleted.size)
        val coordinatorLayout = view!!.findViewById<View>(R.id.coordinatorLayout)
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { undoDeletion(deleted) }
                .show()
    }

    private fun deleteItems(deletedIds: List<Long>): MutableList<T> {
        val deleted = deletedIds
                .map { id -> adapter!!.getItemById(id) }
                .filter { item -> item != null }
                .map { it!! }
                .toMutableList()
        for (item in deleted) {
            adapter!!.removeItem(item)
            item.delete()
        }
        adapter!!.notifyDataSetChanged()
        reloadData()
        return deleted
    }

    private fun undoDeletion(deleted: MutableList<T>) {
        for (item in deleted) {
            item.saveRecursively()
            adapter!!.addItem(item)
        }
        reloadData()
        deleted.clear()
    }

    override fun onClick(holder: SelectableViewHolder<T>, item: T) {
        if (!actionModeCallback!!.click(holder)) {
            onSelected(item)
        }
    }

    override fun onLongClick(holder: SelectableViewHolder<T>) {
        actionModeCallback!!.longClick(holder)
    }

    protected abstract fun onSelected(item: T)

    companion object {

        val ITEM_ID = "id"
    }

}
