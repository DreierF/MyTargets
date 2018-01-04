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

package de.dreier.mytargets.features.arrows

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.databinding.FragmentArrowsBinding
import de.dreier.mytargets.databinding.ItemImageDetailsBinding
import de.dreier.mytargets.shared.models.dao.ArrowDAO
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.utils.DividerItemDecoration
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

class EditArrowListFragment : EditableListFragmentBase<Arrow, SimpleListAdapterBase<Arrow>>() {

    private lateinit var binding: FragmentArrowsBinding

    init {
        itemTypeDelRes = R.plurals.arrow_deleted
        actionModeCallback = ItemActionModeCallback(this, selector, R.plurals.arrow_selected)
        actionModeCallback?.setEditCallback({ this.onEdit(it) })
        actionModeCallback?.setDeleteCallback({ this.onDelete(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            navigationController.navigateToCreateArrow()
                    .fromFab(binding.fab)
                    .start()
        }
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_arrows, container, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
                DividerItemDecoration(context!!, R.drawable.full_divider))
        adapter = ArrowAdapter()
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onLoad(args: Bundle?): LoaderUICallback {
        val arrows = ArrowDAO.loadArrows()
        return {
            adapter!!.setList(arrows.toMutableList())
            binding.emptyState!!.root.visibility = if (arrows.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onEdit(itemId: Long) {
        navigationController.navigateToEditArrow(itemId)
                .start()
    }

    override fun onSelected(item: Arrow) {
        navigationController.navigateToEditArrow(item.id)
                .start()
    }

    override fun deleteItem(item: Arrow): () -> Arrow {
        val images = ArrowDAO.loadArrowImages(item.id)
        ArrowDAO.deleteArrow(item)
        return {
            ArrowDAO.saveArrow(item, images)
            item
        }
    }

    private inner class ArrowAdapter : SimpleListAdapterBase<Arrow>() {

        public override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_details, parent, false)
            return ViewHolder(itemView)
        }
    }

    internal inner class ViewHolder(itemView: View) : SelectableViewHolder<Arrow>(itemView, selector, this@EditArrowListFragment, this@EditArrowListFragment) {
        private val binding: ItemImageDetailsBinding = DataBindingUtil.bind(itemView)

        override fun bindItem(item: Arrow) {
            binding.name.text = item.name
            binding.image.setImageDrawable(item.drawable)
        }
    }
}

