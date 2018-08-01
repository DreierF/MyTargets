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

package de.dreier.mytargets.features.training.overview

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.base.viewmodel.ViewModelFactory
import de.dreier.mytargets.databinding.FragmentTrainingsBinding
import de.dreier.mytargets.databinding.ItemTrainingBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.edit.EditTrainingFragment.Companion.CREATE_FREE_TRAINING_ACTION
import de.dreier.mytargets.features.training.edit.EditTrainingFragment.Companion.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.DividerItemDecoration
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

/**
 * Shows an overview over all training days
 */
class TrainingsFragment : ExpandableListFragment<Header, Training>() {

    private lateinit var binding: FragmentTrainingsBinding

    private lateinit var viewModel: TrainingsViewModel

    init {
        itemTypeDelRes = R.plurals.training_deleted
        actionModeCallback = ItemActionModeCallback(
            this, selector,
            R.plurals.training_selected
        )
        actionModeCallback?.setEditCallback(this::onEdit)
        actionModeCallback?.setDeleteCallback(this::onDelete)
        actionModeCallback?.setStatisticsCallback(this::onStatistics)
    }

    override fun onResume() {
        super.onResume()
        binding.fabSpeedDial.closeMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trainings, container, false)
        binding.recyclerView.setHasFixedSize(true)
        adapter = TrainingAdapter(context!!)
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(context!!, R.drawable.full_divider)
        )
        binding.fabSpeedDial.setMenuListener { menuItem ->
            when (menuItem.itemId) {
                R.id.fab1 -> navigationController
                    .navigateToCreateTraining(CREATE_FREE_TRAINING_ACTION)
                    .fromFab(
                        binding.fabSpeedDial
                            .getFabFromMenuId(R.id.fab1), R.color.fabFreeTraining,
                        R.drawable.fab_trending_up_white_24dp
                    )
                    .start()
                R.id.fab2 -> navigationController
                    .navigateToCreateTraining(CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)
                    .fromFab(
                        binding.fabSpeedDial
                            .getFabFromMenuId(R.id.fab2), R.color.fabTrainingWithStandardRound,
                        R.drawable.fab_album_24dp
                    )
                    .start()
                else -> {
                }
            }
            false
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val factory = ViewModelFactory(activity!!.application!!)
        viewModel = ViewModelProviders.of(this, factory).get(TrainingsViewModel::class.java)
        viewModel.trainings.observe(this, Observer { trainings ->
            if (trainings != null) {
                this@TrainingsFragment.setList(trainings, false)
                activity?.invalidateOptionsMenu()
                binding.emptyState.root.visibility =
                        if (trainings.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.statistics, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val showStatistics = adapter != null && adapter!!.itemCount > 0
        menu.findItem(R.id.action_statistics).isVisible = showStatistics
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_statistics -> {
                navigationController.navigateToStatistics(viewModel.getAllRoundIds())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onSelected(item: Training) {
        navigationController.navigateToTraining(item)
            .start()
    }

    private fun onStatistics(ids: List<Long>) {
        navigationController.navigateToStatistics(viewModel.getRoundIds(ids))
    }

    private fun onEdit(itemId: Long) {
        navigationController.navigateToEditTraining(itemId)
    }

    override fun deleteItem(item: Training) = viewModel.deleteTraining(item)

    private inner class TrainingAdapter internal constructor(
        context: Context
    ) : ExpandableListAdapter<Header, Training>(
        { child ->
            Utils.getMonthHeader(context, child.date)
        }, compareByDescending(Header::id),
        compareByDescending(Training::date).thenByDescending(Training::id)
    ) {

        override fun getSecondLevelViewHolder(parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_training, parent, false)
            return ViewHolder(itemView)
        }
    }

    private inner class ViewHolder(itemView: View) : SelectableViewHolder<Training>(
        itemView,
        selector,
        this@TrainingsFragment,
        this@TrainingsFragment
    ) {
        internal var binding = ItemTrainingBinding.bind(itemView)

        override fun bindItem(item: Training) {
            binding.training.text = item.title
            binding.trainingDate.text = item.formattedDate
            binding.gesTraining.text = item.score.format(
                Utils.getCurrentLocale(context!!),
                SettingsManager.scoreConfiguration
            )
        }
    }
}
