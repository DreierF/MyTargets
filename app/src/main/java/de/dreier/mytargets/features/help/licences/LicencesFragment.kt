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

package de.dreier.mytargets.features.help.licences

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.FragmentListBinding
import de.dreier.mytargets.utils.DividerItemDecoration
import de.dreier.mytargets.utils.SlideInItemAnimator
import me.oriley.homage.Homage
import me.oriley.homage.recyclerview.HomageAdapter
import me.oriley.homage.recyclerview.HomageView

class LicencesFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private var layoutManager: RecyclerView.LayoutManager? = null

    private fun createAdapter(): RecyclerView.Adapter<*> {
        val homage = Homage(activity!!, R.raw.licences)

        // Adds a custom license definition to enable matching in your JSON list
        homage.addLicense(
            "epl", R.string.license_epl_name, R.string.license_epl_url,
            R.string.license_epl_description
        )

        homage.refreshLibraries()

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(context!!, R.drawable.full_divider)
        )
        binding.fab.visibility = View.GONE
        return HomageAdapter(homage, HomageView.ExtraInfoMode.EXPANDABLE, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.recyclerView.setHasFixedSize(true)
        val adapter = createAdapter()
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter
        layoutManager = binding.recyclerView.layoutManager

        if (savedInstanceState != null) {
            val layoutState = savedInstanceState.getParcelable<Parcelable>(KEY_LAYOUT_MANAGER_STATE)
            layoutManager!!.onRestoreInstanceState(layoutState)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (layoutManager != null) {
            outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManager!!.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_LAYOUT_MANAGER_STATE = "layoutManagerState"
    }
}
