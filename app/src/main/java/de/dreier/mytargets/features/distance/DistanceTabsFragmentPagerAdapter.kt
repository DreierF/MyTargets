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

package de.dreier.mytargets.features.distance

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentPagerAdapter
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.*

class DistanceTabsFragmentPagerAdapter(
        private val context: FragmentActivity,
        distance: Dimension) : FragmentPagerAdapter(context.supportFragmentManager) {

    private val fragments = arrayOf(
            DistanceGridFragment.newInstance(distance, UNITS[0]),
            DistanceGridFragment.newInstance(distance, UNITS[1]),
            DistanceGridFragment.newInstance(distance, UNITS[2])
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.metric)
            1 -> context.getString(R.string.imperial)
            else -> context.getString(R.string.us)
        }
    }

    companion object {
        val UNITS = listOf(METER, YARDS, FEET)
    }
}
