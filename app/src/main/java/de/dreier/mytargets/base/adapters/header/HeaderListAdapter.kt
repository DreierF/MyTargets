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

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.ItemHeaderBinding
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder
import java.util.*

abstract class HeaderListAdapter<C : IIdProvider>(parentPartition: PartitionDelegate<SimpleHeader, C>, childComparator: Comparator<C>) : HeaderListAdapterBase<HeaderListAdapter.SimpleHeader, C, HeaderListAdapterBase.HeaderHolder<HeaderListAdapter.SimpleHeader, C>>(parentPartition, Comparator { obj, other -> obj.compareTo(other) }, childComparator) {

    override fun getHeaderHolder(parent: SimpleHeader, childComparator: Comparator<C>): HeaderListAdapterBase.HeaderHolder<SimpleHeader, C> {
        return HeaderListAdapterBase.HeaderHolder(parent, childComparator)
    }

    override fun getItemPosition(item: C): Int {
        var pos = 0
        for (header in headersList) {
            if (header.totalItemCount < 1) {
                continue
            }
            pos++
            for (child in header.children) {
                if (child == item) {
                    return pos
                }
                pos++
            }
        }
        return -1
    }

    override fun getTopLevelViewHolder(parent: ViewGroup): HeaderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
        return HeaderViewHolder(itemView)
    }

    inner class HeaderViewHolder internal constructor(itemView: View) : HeaderBindingHolder<SimpleHeader>(itemView) {
        private val binding: ItemHeaderBinding = DataBindingUtil.bind(itemView)

        override fun bindItem(item: SimpleHeader) {
            binding.header.text = item.title
        }
    }

    class SimpleHeader(index: Long, internal var title: String) : IIdProvider, Comparable<SimpleHeader> {
        override var id: Long = index

        override fun compareTo(other: SimpleHeader): Int {
            return id.compareTo(other.id)
        }

        override fun toString(): String {
            return title
        }
    }
}
