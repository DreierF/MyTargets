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

package de.dreier.mytargets.features.bows

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.databinding.FragmentBowsBinding
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.EBowType.*
import de.dreier.mytargets.shared.models.dao.BowDAO
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.utils.DividerItemDecoration
import de.dreier.mytargets.utils.SlideInItemAnimator

class EditBowListFragment : EditableListFragmentBase<Bow, SimpleListAdapterBase<Bow>>() {

    private lateinit var binding: FragmentBowsBinding

    init {
        itemTypeDelRes = R.plurals.bow_deleted
        actionModeCallback = ItemActionModeCallback(this, selector,
                R.plurals.bow_selected)
        actionModeCallback?.setEditCallback(this::onEdit)
        actionModeCallback?.setDeleteCallback(this::onDelete)
    }

    override fun onResume() {
        super.onResume()
        binding.fabSpeedDial.closeMenu()
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bows, container, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
                DividerItemDecoration(context!!, R.drawable.full_divider))
        adapter = BowAdapter(selector, this, this)
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter

        binding.fabSpeedDial.setMenuListener { menuItem ->
            val itemId = menuItem.itemId
            val bowType = bowTypeMap.get(itemId)
            val fab = binding.fabSpeedDial.getFabFromMenuId(itemId)
            navigationController.navigateToCreateBow(bowType)
                    .fromFab(fab, R.color.fabBow, bowType.drawable)
                    .start()
            false
        }

        return binding.root
    }

    override fun onLoad(args: Bundle?): LoaderUICallback {
        val bows = BowDAO.loadBows()
        return {
            adapter!!.setList(bows.toMutableList())
            binding.emptyState!!.root.visibility = if (bows.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onEdit(itemId: Long) {
        navigationController.navigateToEditBow(itemId)
    }

    override fun onSelected(item: Bow) {
        navigationController.navigateToEditBow(item.id)
    }

    override fun deleteItem(item: Bow): () -> Bow {
        val images = BowDAO.loadBowImages(item.id)
        val sightMarks = BowDAO.loadSightMarks(item.id)
        BowDAO.deleteBow(item)
        return {
            BowDAO.saveBow(item, images, sightMarks)
            item
        }
    }

    companion object {
        internal var bowTypeMap = SparseArray<EBowType>()

        init {
            bowTypeMap.put(R.id.fabBowRecurve, RECURVE_BOW)
            bowTypeMap.put(R.id.fabBowCompound, COMPOUND_BOW)
            bowTypeMap.put(R.id.fabBowBare, BARE_BOW)
            bowTypeMap.put(R.id.fabBowLong, LONG_BOW)
            bowTypeMap.put(R.id.fabBowHorse, HORSE_BOW)
            bowTypeMap.put(R.id.fabBowYumi, YUMI)
        }
    }
}
