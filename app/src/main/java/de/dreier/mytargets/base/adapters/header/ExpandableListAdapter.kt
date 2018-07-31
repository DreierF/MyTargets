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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.ItemHeaderExpandableBinding
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.multiselector.ExpandableHeaderBindingHolder
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder

typealias PartitionDelegate<PARENT, CHILD> = (CHILD) -> PARENT

abstract class ExpandableListAdapter<P : IIdProvider, C : IIdProvider>(
    partitionDelegate: PartitionDelegate<P, C>,
    headerComparator: Comparator<P>, childComparator: Comparator<C>
) : HeaderListAdapterBase<P, C, ExpandableHeaderHolder<P, C>>(
    partitionDelegate,
    headerComparator,
    childComparator
) {

    var expandedIds: List<Long>
        get() = headersList
            .filter { it.expanded }
            .map { it.item.id }
        set(expanded) {
            headersList.indices
                .map { headersList[it] }
                .forEach { it.expanded = expanded.contains(it.item.id) }
        }

    override fun onBindViewHolder(viewHolder: ItemBindingHolder<IIdProvider>, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        if (viewHolder is ExpandableHeaderBindingHolder<*>) {
            val header = getHeaderForPosition(position)
            (viewHolder as ExpandableHeaderBindingHolder<*>)
                .setExpandOnClickListener(
                    View.OnClickListener { expandOrCollapse(header) },
                    header.expanded
                )
        }
    }

    override fun getItemPosition(item: C): Int {
        var pos = 0
        for (header in headersList) {
            if (header.totalItemCount < 1) {
                continue
            }
            pos++
            if (header.totalItemCount == 1) {
                continue
            }
            for (child in header.children) {
                if (child == item) {
                    return pos
                }
                pos++
            }
        }
        return -1
    }

    fun ensureItemIsExpanded(item: C) {
        val parentHolder = getHeaderHolderForChild(item)
        val pos = getHeaderIndex(parentHolder)
        if (pos >= 0 && !headersList[pos].expanded) {
            expandOrCollapse(headersList[pos])
        }
    }

    private fun expandOrCollapse(header: ExpandableHeaderHolder<P, C>) {
        val childLength = header.children.size
        if (!header.expanded) {
            notifyItemRangeInserted(getAbsolutePosition(header) + 1, childLength)
        } else {
            notifyItemRangeRemoved(getAbsolutePosition(header) + 1, childLength)
        }
        header.expanded = !header.expanded
    }

    override fun setList(list: List<C>) {
        val oldExpanded = expandedIds
        fillChildMap(list)
        expandedIds = oldExpanded
        notifyDataSetChanged()
    }

    fun setList(children: List<C>, opened: Boolean) {
        fillChildMap(children)
        expandAll(opened)
        notifyDataSetChanged()
    }

    private fun expandAll(expanded: Boolean) {
        for (header in headersList) {
            header.expanded = expanded
        }
    }

    fun expandFirst() {
        if (!headersList[0].expanded) {
            expandOrCollapse(headersList[0])
        }
    }

    private fun getAbsolutePosition(h: ExpandableHeaderHolder<P, C>): Int {
        val headerIndex = getHeaderIndex(h)
        return (0 until headerIndex).sumBy { headersList[it].totalItemCount }
    }

    override fun getHeaderHolder(
        parent: P,
        childComparator: Comparator<C>
    ): ExpandableHeaderHolder<P, C> {
        return ExpandableHeaderHolder(parent, childComparator)
    }

    override fun getTopLevelViewHolder(parent: ViewGroup): HeaderViewHolder<P> {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_header_expandable, parent, false)
        return HeaderViewHolder(itemView)
    }

    class HeaderViewHolder<P> internal constructor(itemView: View) :
        ExpandableHeaderBindingHolder<P>(itemView, R.id.expand_collapse) {
        private val binding = ItemHeaderExpandableBinding.bind(itemView)

        override fun bindItem(item: P) {
            binding.header.text = item.toString()
        }
    }

}
