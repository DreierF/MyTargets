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

package de.dreier.mytargets.features.training.target

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter
import de.dreier.mytargets.base.adapters.header.HeaderListAdapter
import de.dreier.mytargets.base.fragments.SelectItemFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.databinding.FragmentTargetSelectBinding
import de.dreier.mytargets.databinding.ItemImageSimpleBinding
import de.dreier.mytargets.features.training.target.TargetListFragment.EFixedType.*
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.TargetFactory
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder
import junit.framework.Assert
import java.util.*

class TargetListFragment : SelectItemFragmentBase<Target, ExpandableListAdapter<HeaderListAdapter.SimpleHeader, Target>>(), AdapterView.OnItemSelectedListener {
    private var binding: FragmentTargetSelectBinding? = null
    private var scoringStyleAdapter: ArrayAdapter<String>? = null
    private var targetSizeAdapter: ArrayAdapter<String>? = null

    private val themedSpinnerAdapter: ArrayAdapter<String>
        get() {
            val actionBar = (activity as AppCompatActivity).supportActionBar
            Assert.assertNotNull(actionBar)
            val themedContext = actionBar!!.themedContext
            val spinnerAdapter = ArrayAdapter(themedContext,
                    android.R.layout.simple_spinner_item, ArrayList<String>())
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return spinnerAdapter
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_target_select, container, false)
        adapter = TargetAdapter()
        binding!!.recyclerView.itemAnimator = SlideInItemAnimator()
        binding!!.recyclerView.adapter = adapter

        useDoubleClickSelection = true
        ToolbarUtils.setSupportActionBar(this, binding!!.toolbar)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Needs activity context
        scoringStyleAdapter = themedSpinnerAdapter
        binding!!.scoringStyle.adapter = scoringStyleAdapter
        targetSizeAdapter = themedSpinnerAdapter
        binding!!.targetSize.adapter = targetSizeAdapter

        // Process passed arguments
        val target = arguments!!.getParcelable<Target>(ITEM)
        val fixedType = EFixedType
                .valueOf(arguments!!.getString(FIXED_TYPE, NONE.name))
        val list = when (fixedType) {
            NONE -> TargetFactory.getList()
            TARGET -> listOf(target!!.model)
            GROUP -> TargetFactory.getList(target!!)
        }
        val targets = list
                .map { value -> Target(value.id, 0) }
                .toMutableList()
        adapter!!.setList(targets)
        selectItem(binding!!.recyclerView, target!!)

        updateSettings()

        // Set initial target size
        val diameters = target.model.diameters
        val diameterIndex = diameters.indices.firstOrNull { diameters[it] == target.diameter } ?: -1

        setSelectionWithoutEvent(binding!!.scoringStyle, target.scoringStyleIndex)
        setSelectionWithoutEvent(binding!!.targetSize, diameterIndex)
    }

    override fun selectItem(recyclerView: RecyclerView, item: Target) {
        adapter!!.ensureItemIsExpanded(item)
        super.selectItem(recyclerView, item)
    }

    override fun onClick(holder: SelectableViewHolder<Target>, item: Target?) {
        super.onClick(holder, item)
        if (item == null) {
            return
        }
        updateSettings()
        saveItem()
    }

    private fun updateSettings() {
        // Init scoring styles
        val target = adapter!!.getItemById(selector.getSelectedId()!!)
        val styles = target!!.model.scoringStyles.map { it.toString() }
        updateAdapter(binding!!.scoringStyle, scoringStyleAdapter!!, styles)

        // Init target size spinner
        val diameters = diameterToList(target.model.diameters)
        updateAdapter(binding!!.targetSize, targetSizeAdapter!!, diameters)
        if (diameters.size > 1) {
            binding!!.targetSize.visibility = View.VISIBLE
        } else {
            binding!!.targetSize.visibility = View.GONE
        }
    }

    private fun updateAdapter(spinner: Spinner, spinnerAdapter: ArrayAdapter<String>, strings: List<String>) {
        val lastSelection = spinner.selectedItemPosition
        spinnerAdapter.clear()
        spinnerAdapter.addAll(strings)
        val position = if (lastSelection < strings.size) lastSelection else strings.size - 1
        setSelectionWithoutEvent(spinner, position)
    }

    private fun setSelectionWithoutEvent(spinner: Spinner, position: Int) {
        spinner.onItemSelectedListener = null
        spinner.setSelection(position, false)
        spinner.onItemSelectedListener = this
    }

    private fun diameterToList(diameters: Array<Dimension>): List<String> {
        return diameters.map { it.toString() }
    }

    override fun onSave(): Target {
        val target = super.onSave()
        target.scoringStyleIndex = binding!!.scoringStyle.selectedItemPosition
        val diameters = target.model.diameters
        target.diameter = diameters[binding!!.targetSize.selectedItemPosition]
        arguments!!.putParcelable(ITEM, target)
        return target
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, id: Long) {
        updateSettings()
        saveItem()
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    enum class EFixedType {
        /**
         * The user has completely free choice from all target faces.
         */
        NONE,

        /**
         * The user can change the selection within a group of target faces
         * like between the WA target faces.
         */
        GROUP,

        /**
         * The user cannot change the selected target face, but e.g. scoring style or diameter.
         */
        TARGET
    }

    private inner class TargetAdapter internal constructor() : ExpandableListAdapter<HeaderListAdapter.SimpleHeader, Target>({ child ->
        val type = child.model.type
        HeaderListAdapter.SimpleHeader(type.ordinal.toLong(), type.toString())
    }, compareBy { it }, TargetFactory.comparator) {

        override fun getSecondLevelViewHolder(parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_simple, parent, false)
            return ViewHolder(itemView)
        }
    }

    private inner class ViewHolder(itemView: View) : SelectableViewHolder<Target>(itemView, selector, this@TargetListFragment) {
        private val binding: ItemImageSimpleBinding = DataBindingUtil.bind(itemView)

        override fun bindItem(item: Target) {
            binding.name.text = item.model.toString()
            binding.image.setImageDrawable(item.drawable)
        }
    }

    companion object {
        const val FIXED_TYPE = "fixed_type"
    }
}
