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

package de.dreier.mytargets.base.adapters.header

import android.view.ViewGroup
import de.dreier.mytargets.base.adapters.ListAdapterBase
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder
import java.util.*

abstract class HeaderListAdapterBase<P : IIdProvider, C : IIdProvider, H : HeaderListAdapterBase.HeaderHolder<P, C>>(
        private val partitionDelegate: PartitionDelegate<P, C>,
        private val headerComparator: Comparator<P>,
        private val childComparator: Comparator<C>
) : ListAdapterBase<ItemBindingHolder<IIdProvider>, C>() {

    protected var headersList: MutableList<H> = ArrayList()

    init {
        super.setHasStableIds(true)
    }

    override fun getItem(position: Int): C? {
        val header = getHeaderForPosition(position)
        val pos = getHeaderRelativePosition(position)
        return if (pos != 0) {
            header.children[pos - 1]
        } else null
    }

    override fun getItemId(position: Int): Long {
        val header = getHeaderForPosition(position)
        val pos = getHeaderRelativePosition(position)
        return if (pos == 0) {
            header.item.id
        } else {
            header.children[pos - 1].id
        }
    }

    override fun getItemCount(): Int {
        return headersList.sumBy { it.totalItemCount }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getHeaderRelativePosition(position) == 0) HEADER_TYPE else ITEM_TYPE
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBindingHolder<IIdProvider> {
        return if (viewType == HEADER_TYPE) {
            getTopLevelViewHolder(parent) as ItemBindingHolder<IIdProvider>
        } else {
            getSecondLevelViewHolder(parent) as ItemBindingHolder<IIdProvider>
        }
    }

    protected abstract fun getTopLevelViewHolder(parent: ViewGroup): HeaderBindingHolder<P>

    protected abstract fun getSecondLevelViewHolder(parent: ViewGroup): SelectableViewHolder<C>

    override fun onBindViewHolder(viewHolder: ItemBindingHolder<IIdProvider>, position: Int) {
        val header = getHeaderForPosition(position)
        val pos = getHeaderRelativePosition(position)
        if (pos == 0) {
            viewHolder.internalBindItem(header.item)
        } else {
            viewHolder.internalBindItem(header.children[pos - 1])
        }
    }

    protected fun getHeaderForPosition(position: Int): H {
        var items = 0
        for (header in headersList) {
            if (header.totalItemCount > position - items) {
                return header
            }
            items += header.totalItemCount
        }
        throw IllegalStateException("Position is not in list!")
    }

    private fun getHeaderRelativePosition(position: Int): Int {
        var items = 0
        for (header in headersList) {
            val relativePos = position - items
            if (header.totalItemCount > relativePos) {
                return relativePos
            }
            items += header.totalItemCount
        }
        throw IllegalStateException("Position is not in list!")
    }

    override fun addItem(item: C) {
        addChildToMap(item)
        notifyDataSetChanged()
    }

    override fun removeItem(item: C) {
        val parent = partitionDelegate.invoke(item)
        val headerIndex = getHeaderIndex(getHeaderHolder(parent, childComparator))
        if (headerIndex < 0) {
            return
        }
        val header = headersList[headerIndex]
        header.remove(item)
        if (header.children.isEmpty()) {
            headersList.remove(header)
        }
    }

    override fun setList(list: MutableList<C>) {
        fillChildMap(list)
        notifyDataSetChanged()
    }

    protected fun fillChildMap(children: List<C>) {
        headersList.clear()
        for (child in children) {
            addChildToMap(child)
        }
    }

    private fun addChildToMap(child: C) {
        val parentHolder = getHeaderHolderForChild(child)
        val pos = getHeaderIndex(parentHolder)
        if (pos < 0) {
            parentHolder.add(child)
            headersList.add(-pos - 1, parentHolder)
        } else {
            headersList[pos].add(child)
        }
    }

    protected fun getHeaderHolderForChild(child: C): H {
        val parent = partitionDelegate.invoke(child)
        return getHeaderHolder(parent, childComparator)
    }

    protected abstract fun getHeaderHolder(parent: P, childComparator: Comparator<C>): H

    override fun getItemById(id: Long): C? {
        return headersList
                .flatMap { it.children }
                .firstOrNull { it.id == id }
    }

    internal fun getHeaderIndex(h: H): Int {
        return Collections.binarySearch(headersList, h
        ) { holder1, holder2 -> headerComparator.compare(holder1.item, holder2.item) }
    }

    open class HeaderHolder<HEADER, CHILD> internal constructor(internal var item: HEADER, private val childComparator: Comparator<in CHILD>) {
        internal var children: MutableList<CHILD> = mutableListOf()

        /**
         * Returns the number of items contained in this header including the header itself.
         * This number is always greater or equal 1.
         */
        open val totalItemCount: Int
            get() = 1 + children.size

        fun add(item: CHILD) {
            val pos = children.binarySearch(item, childComparator)
            if (pos < 0) {
                children.add(-pos - 1, item)
            } else {
                throw IllegalArgumentException("Item must not be inserted twice!")
            }
        }

        fun remove(item: CHILD) {
            children.remove(item)
        }
    }

    companion object {
        private const val ITEM_TYPE = 2
        private const val HEADER_TYPE = 1
    }
}
