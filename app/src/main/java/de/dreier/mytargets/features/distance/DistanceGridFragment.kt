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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.base.fragments.SelectItemFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.base.viewmodel.ViewModelFactory
import de.dreier.mytargets.databinding.FragmentListBinding
import de.dreier.mytargets.databinding.ItemDistanceBinding
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

class DistanceGridFragment : SelectItemFragmentBase<Dimension, SimpleListAdapterBase<Dimension>>(),
    DistanceInputDialog.OnClickListener {
    private lateinit var binding: FragmentListBinding
    private lateinit var unit: Dimension.Unit

    private lateinit var viewModel: DistancesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(activity, 3)
        binding.recyclerView.addItemDecoration(DistanceItemDecorator(activity!!, 3))
        adapter = DistanceAdapter()
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener {
            DistanceInputDialog.Builder(context!!)
                .setUnit(unit.toString())
                .setOnClickListener(this@DistanceGridFragment)
                .show()
        }
        return binding.root
    }

    override fun onOkClickListener(input: String) {
        val distance = viewModel.createDistanceFromInput(input)
        navigationController.setResultSuccess(distance)
        navigationController.finish()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = ViewModelFactory(activity!!.application!!)
        viewModel = ViewModelProviders.of(this, factory).get(DistancesViewModel::class.java)
        val bundle = arguments!!
        val distance: Dimension = bundle.getParcelable(ITEM)!!
        unit = Unit.from(bundle.getString(DISTANCE_UNIT))!!
        viewModel.setUnit(unit)
        viewModel.setDistance(distance)
        viewModel.distances.observe(this, Observer { distances ->
            if (distances != null) {
                adapter.setList(distances)
                selectItem(binding.recyclerView, distance)
            }
        })
    }

    private inner class DistanceAdapter :
        SimpleListAdapterBase<Dimension>(compareBy(Dimension::value)) {
        public override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_distance, parent, false)
            return ViewHolder(itemView)
        }
    }

    internal inner class ViewHolder(itemView: View) :
        SelectableViewHolder<Dimension>(itemView, selector, this@DistanceGridFragment) {
        private val binding = ItemDistanceBinding.bind(itemView)

        override fun bindItem(item: Dimension) {
            binding.distance.text = item.toString()
        }
    }

    companion object {
        private const val DISTANCE_UNIT = "distance_unit"

        fun newInstance(distance: Dimension, unit: Unit): DistanceGridFragment {
            val fragment = DistanceGridFragment()
            val args = Bundle()
            args.putParcelable(ITEM, distance)
            args.putString(DISTANCE_UNIT, unit.toString())
            fragment.arguments = args
            return fragment
        }
    }
}
