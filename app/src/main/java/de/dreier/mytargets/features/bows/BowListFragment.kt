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
import de.dreier.mytargets.base.activities.ItemSelectActivity.Companion.ITEM
import de.dreier.mytargets.base.fragments.FragmentBase
import de.dreier.mytargets.base.fragments.FragmentBase.LoaderUICallback
import de.dreier.mytargets.base.fragments.SelectPureListItemFragmentBase
import de.dreier.mytargets.shared.models.db.Bow

class BowListFragment : SelectPureListItemFragmentBase<Bow>() {

    override fun onLoad(args: Bundle?): FragmentBase.LoaderUICallback {
        val bows = Bow.all
        return LoaderUICallback {
            adapter!!.setList(bows.toMutableList())
            val bow = arguments!!.getParcelable<Bow>(ITEM)
            selectItem(binding.recyclerView, bow!!)
        }
    }
}
