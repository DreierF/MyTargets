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

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.evernote.android.state.State
import de.dreier.mytargets.base.adapters.ListAdapterBase
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.SingleSelectorBundler
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder
import de.dreier.mytargets.utils.multiselector.SingleSelector
import junit.framework.Assert

/**
 * Base class for handling single item selection
 *
 *
 * Parent activity must implement [OnItemSelectedListener].
 *
 * @param <T> Model of the item which is managed within the fragment.
</T> */
abstract class SelectItemFragmentBase<T, U : ListAdapterBase<out ItemBindingHolder<*>, T>> : ListFragmentBase<T, U>() where T : IIdProvider, T : Comparable<T>, T : Parcelable {

    /**
     * Selector which manages the item selection
     */
    @State(SingleSelectorBundler::class)
    var selector = SingleSelector()

    /**
     * Set to true when items are expanded when they are clicked and
     * selected only after hitting them the second time.
     */
    protected var useDoubleClickSelection = false

    /**
     * {@inheritDoc}
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        selector.setSelectable(true)
    }

    /**
     * Searches the recyclerView's adapter for the given item and sets it as selected.
     * If the position is not visible on the screen it is scrolled into view.
     *
     * @param recyclerView RecyclerView instance
     * @param item         Currently selected item
     */
    protected open fun selectItem(recyclerView: RecyclerView, item: T) {
        selector.setSelected(item.id!!, true)
        recyclerView.post {
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val first = manager.findFirstCompletelyVisibleItemPosition()
            val last = manager.findLastCompletelyVisibleItemPosition()
            val position = adapter!!.getItemPosition(item)
            if (first > position || last < position) {
                recyclerView.scrollToPosition(position)
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        Assert.assertNotNull(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun onClick(holder: SelectableViewHolder<T>, item: T) {
        val alreadySelected = selector.isSelected(holder.itemIdentifier)
        selector.setSelected(holder, true)
        if (alreadySelected || !useDoubleClickSelection) {
            saveItem()
            finish()
        }
    }

    /**
     * Returns the selected item to the calling activity. The item is retrieved by calling onSave().
     */
    protected fun saveItem() {
        listener!!.onItemSelected(onSave())
    }

    /**
     * Gets the item that has been selected by the user
     *
     * @return The selected item
     */
    protected open fun onSave(): T {
        return adapter!!.getItemById(selector.getSelectedId()!!)!!
    }
}
