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

package de.dreier.mytargets.base.adapters

import android.view.ViewGroup
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder
import java.util.*

/**
 * The list is automatically sorted in natural order.
 */
abstract class SimpleListAdapterBase<T>(
        private val comparator: Comparator<T>
) : ListAdapterBase<SelectableViewHolder<T>, T>() where T : IIdProvider {

    private var list: MutableList<T> = ArrayList()

    init {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position) + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder<T> {
        return onCreateViewHolder(parent)
    }

    protected abstract fun onCreateViewHolder(parent: ViewGroup): SelectableViewHolder<T>

    override fun onBindViewHolder(viewHolder: SelectableViewHolder<T>, position: Int) {
        viewHolder.internalBindItem(list[position])
    }

    override fun setList(list: MutableList<T>) {
        list.sortWith(comparator)
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): T? {
        return list[position]
    }

    override fun addItem(item: T) {
        val pos = list.binarySearch(item, comparator)
        if (pos < 0) {
            list.add(-pos - 1, item)
            notifyItemInserted(-pos - 1)
        } else {
            list.add(pos, item)
            notifyItemInserted(pos)
        }
    }

    override fun getItemPosition(item: T): Int {
        val pos = list.binarySearch(item, comparator)
        return if (pos >= 0) {
            pos
        } else {
            -1
        }
    }

    override fun removeItem(item: T) {
        val pos = list.binarySearch(item, comparator)
        if (pos < 0) {
            throw IllegalArgumentException("Item has already been removed!")
        }
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun getItemById(id: Long): T? {
        return list.firstOrNull { it.id == id }
    }
}
