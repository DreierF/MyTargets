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

package de.dreier.mytargets.features.training.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.text.InputType
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.base.viewmodel.ViewModelFactory
import de.dreier.mytargets.databinding.FragmentTrainingBinding
import de.dreier.mytargets.databinding.ItemRoundBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.utils.*
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

/**
 * Shows all rounds of one training.
 */
open class TrainingFragment : EditableListFragmentBase<Round, SimpleListAdapterBase<Round>>() {

    private lateinit var binding: FragmentTrainingBinding
    private var trainingId: Long = 0

    private lateinit var viewModel: TrainingViewModel
    private val factory = ViewModelFactory()

    init {
        itemTypeDelRes = R.plurals.round_deleted
        actionModeCallback = ItemActionModeCallback(
            this, selector,
            R.plurals.round_selected
        )
        actionModeCallback?.setEditCallback { this.onEdit(it) }
        actionModeCallback?.setStatisticsCallback { this.onStatistics(it) }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(context!!, R.drawable.full_divider)
        )
        adapter = RoundAdapter(BooleanArray(2))
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter

        binding.fab.visibility = View.GONE
        binding.fab.setOnClickListener {
            // New round to free training
            navigationController.navigateToCreateRound(trainingId, binding.fab)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this, factory).get(TrainingViewModel::class.java)
        trainingId = arguments.getLongOrNull(EditableListFragmentBase.ITEM_ID)!!
        viewModel.setTrainingId(trainingId)
//        binding.training = viewModel
        viewModel.training.observe(this, Observer { training1 ->
            if (training1 == null) {
                return@Observer
            }
            // Hide fab for standard rounds
            val supportsDeletion = training1.standardRoundId == null
            if (supportsDeletion) {
                actionModeCallback?.setDeleteCallback { this.onDelete(it) }
            } else {
                actionModeCallback?.setDeleteCallback(null)
            }
            binding.fab.visibility = if (supportsDeletion) View.VISIBLE else View.GONE

            // Set round info
            val colorDrawable = if (training1.environment.indoor) {
                R.drawable.ic_house_24dp
            } else {
                training1.environment.weather.colorDrawable
            }
            binding.weatherIcon.setImageResource(colorDrawable)

            activity!!.invalidateOptionsMenu()

            ToolbarUtils.setTitle(this@TrainingFragment, training1.title)
            ToolbarUtils.setSubtitle(this@TrainingFragment, training1.formattedDate)
        })
        viewModel.trainingAndRounds.observe(this, Observer { trainingAndRounds ->
            if (trainingAndRounds == null) {
                return@Observer
            }

            val equals = BooleanArray(2)
            binding.detailRoundInfo.text = TrainingInfoUtils.getTrainingInfo(
                context!!,
                trainingAndRounds.first,
                trainingAndRounds.second,
                equals
            )

            adapter = RoundAdapter(equals)
            binding.recyclerView.adapter = adapter
            adapter!!.setList(trainingAndRounds.second)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_scoreboard).isVisible = viewModel.training.value != null
        menu.findItem(R.id.action_statistics).isVisible = viewModel.training.value != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scoreboard -> {
                navigationController.navigateToScoreboard(trainingId)
                return true
            }
            R.id.action_statistics -> {
                navigationController.navigateToStatistics(trainingId)
                return true
            }
            R.id.action_comment -> {
                MaterialDialog.Builder(context!!)
                    .title(R.string.comment)
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .input("", viewModel.training.value!!.comment) { _, input ->
                        viewModel.setTrainingComment(input.toString())
                    }
                    .negativeText(android.R.string.cancel)
                    .show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSelected(item: Round) {
        navigationController.navigateToRound(item)
            .start()
    }

    private fun onEdit(itemId: Long) {
        navigationController.navigateToEditRound(trainingId, itemId)
    }

    private fun onStatistics(ids: List<Long>) {
        navigationController.navigateToStatistics(ids)
    }

    override fun deleteItem(item: Round) = viewModel.deleteRound(item)

    private inner class RoundAdapter(val equals: BooleanArray) :
        SimpleListAdapterBase<Round>(compareBy(Round::index)) {
        override fun onCreateViewHolder(parent: ViewGroup): SelectableViewHolder<Round> {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_round, parent, false)
            return ViewHolder(itemView, equals)
        }
    }

    private inner class ViewHolder internal constructor(
        itemView: View,
        val equals: BooleanArray
    ) :
        SelectableViewHolder<Round>(
            itemView,
            selector,
            this@TrainingFragment,
            this@TrainingFragment
        ) {
        private val binding = ItemRoundBinding.bind(itemView)

        override fun bindItem(item: Round) {
            binding.title.text = resources.getQuantityString(
                R.plurals.rounds, item
                    .index + 1, item.index + 1
            )
            binding.subtitle.text = TrainingInfoUtils.getRoundInfo(item, equals)
            if (binding.subtitle.text.isEmpty()) {
                binding.subtitle.visibility = View.GONE
            } else {
                binding.subtitle.visibility = View.VISIBLE
            }
            binding.points.text = item.score
                .format(Utils.getCurrentLocale(context!!), SettingsManager.scoreConfiguration)
        }
    }
}
