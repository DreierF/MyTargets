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

import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.databinding.FragmentListBinding
import de.dreier.mytargets.databinding.ItemImageSimpleBinding
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

/**
 *
 */
abstract class SelectPureListItemFragmentBase<T>(
        private val comparator: Comparator<T>
) : SelectItemFragmentBase<T, SimpleListAdapterBase<T>>() where T : IIdProvider, T : Parcelable {

    protected lateinit var binding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter
        binding.fab.visibility = View.GONE
        ToolbarUtils.showUpAsX(this)
        return binding.root
    }

    abstract fun getName(item: T): String

    abstract fun getDrawable(item: T): Drawable

    private inner class ListAdapter : SimpleListAdapterBase<T>(comparator) {

        public override fun onCreateViewHolder(parent: ViewGroup): SelectableViewHolder<T> {
            val inflater = LayoutInflater.from(parent.context)
            return this@SelectPureListItemFragmentBase.onCreateViewHolder(inflater, parent)
        }
    }

    protected fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SelectableViewHolder<T> {
        return ViewHolder(inflater.inflate(R.layout.item_image_simple, parent, false))
    }

    private inner class ViewHolder(itemView: View) : SelectableViewHolder<T>(itemView, selector, this@SelectPureListItemFragmentBase) {
        internal var binding: ItemImageSimpleBinding = DataBindingUtil.bind(itemView)

        override fun bindItem(item: T) {
            binding.name.text = getName(item)
            binding.image.setImageDrawable(getDrawable(item))
        }
    }
}
