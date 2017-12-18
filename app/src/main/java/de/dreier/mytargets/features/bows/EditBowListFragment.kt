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
import de.dreier.mytargets.base.fragments.EditableListFragment
import de.dreier.mytargets.base.fragments.FragmentBase
import de.dreier.mytargets.base.fragments.FragmentBase.LoaderUICallback
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.databinding.FragmentBowsBinding
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.EBowType.*
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.utils.DividerItemDecoration
import de.dreier.mytargets.utils.SlideInItemAnimator

class EditBowListFragment : EditableListFragment<Bow>() {

    private lateinit var binding: FragmentBowsBinding

    init {
        itemTypeDelRes = R.plurals.bow_deleted
        actionModeCallback = ItemActionModeCallback(this, selector,
                R.plurals.bow_selected)
        actionModeCallback.setEditCallback(this::onEdit)
        actionModeCallback.setDeleteCallback(this::onDelete)
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
            EditBowFragment
                    .createIntent(bowType)
                    .withContext(this@EditBowListFragment)
                    .fromFab(fab, R.color.fabBow, bowType.drawable)
                    .start()
            false
        }

        return binding.root
    }

    override fun onLoad(args: Bundle): FragmentBase.LoaderUICallback {
        val bows = Bow.all
        return LoaderUICallback {
            adapter!!.setList(bows)
            binding.emptyState!!.root.visibility = if (bows.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onEdit(itemId: Long) {
        EditBowFragment.editIntent(itemId)
                .withContext(this)
                .start()
    }

    override fun onItemSelected(item: Bow) {
        EditBowFragment.editIntent(item.id!!).withContext(this).start()
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
