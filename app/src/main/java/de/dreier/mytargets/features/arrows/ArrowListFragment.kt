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

package de.dreier.mytargets.features.arrows

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import de.dreier.mytargets.base.fragments.SelectPureListItemFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.base.viewmodel.ViewModelFactory
import de.dreier.mytargets.shared.models.db.Arrow

class ArrowListFragment : SelectPureListItemFragmentBase<Arrow>(compareBy(Arrow::name, Arrow::id)) {

    private lateinit var viewModel: ArrowListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = ViewModelFactory(activity!!.application!!)
        viewModel = ViewModelProviders.of(this, factory).get(ArrowListViewModel::class.java)
        viewModel.arrows.observe(this, Observer { arrows ->
            if (arrows != null) {
                adapter.setList(arrows)
                val arrow = arguments!!.getParcelable<Arrow>(ITEM)
                selectItem(binding.recyclerView, arrow!!)
            }
        })
    }

    override fun getName(item: Arrow) = item.name

    override fun getDrawable(item: Arrow) = item.thumbnail!!.roundDrawable
}
