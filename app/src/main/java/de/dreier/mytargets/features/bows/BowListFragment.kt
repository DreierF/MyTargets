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

import android.os.Bundle
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.base.fragments.SelectPureListItemFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.shared.models.dao.BowDAO
import de.dreier.mytargets.shared.models.db.Bow

class BowListFragment : SelectPureListItemFragmentBase<Bow>(compareBy(Bow::name, Bow::id)) {

    override fun onLoad(args: Bundle?): LoaderUICallback {
        val bows = BowDAO.loadBows()
        return {
            adapter!!.setList(bows.toMutableList())
            val bow = arguments!!.getParcelable<Bow>(ITEM)
            selectItem(binding.recyclerView, bow!!)
        }
    }

    override fun getName(item: Bow) = item.name

    override fun getDrawable(item: Bow) = item.thumbnail!!.roundDrawable

}
