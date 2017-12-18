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

package de.dreier.mytargets.features.distance

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM
import de.dreier.mytargets.databinding.FragmentDistanceBinding
import de.dreier.mytargets.shared.models.Dimension

class DistanceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil
                .inflate<FragmentDistanceBinding>(inflater, R.layout.fragment_distance, container, false)
        val distance = arguments!!.getParcelable<Dimension>(ITEM)
        binding.viewPager.adapter = DistanceTabsFragmentPagerAdapter(activity!!, distance)
        binding.slidingTabs.setupWithViewPager(binding.viewPager)

        // Select current unit
        val item = DistanceTabsFragmentPagerAdapter.UNITS.indexOf(distance!!.unit)
        binding.viewPager.setCurrentItem(item, false)
        return binding.root
    }
}
