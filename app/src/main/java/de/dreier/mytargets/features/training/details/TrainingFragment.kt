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

import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.content.LocalBroadcastManager
import android.text.InputType
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import de.dreier.mytargets.R
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.base.db.dao.TrainingDAO
import de.dreier.mytargets.base.db.dao.RoundDAO
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.fragments.ItemActionModeCallback
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.databinding.FragmentTrainingBinding
import de.dreier.mytargets.databinding.ItemRoundBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.*
import de.dreier.mytargets.utils.MobileWearableClient.Companion.BROADCAST_UPDATE_TRAINING_FROM_REMOTE
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

/**
 * Shows all rounds of one training.
 */
open class TrainingFragment : EditableListFragmentBase<Round, SimpleListAdapterBase<Round>>() {

    private val equals = BooleanArray(2)
    private lateinit var binding: FragmentTrainingBinding
    private var trainingId: Long = 0
    private var training: Training? = null

    private val updateReceiver = object : MobileWearableClient.EndUpdateReceiver() {

        override fun onUpdate(trainingId: Long, roundId: Long, end: End) {
            if (this@TrainingFragment.trainingId == trainingId) {
                reloadData()
            }
        }
    }

    init {
        itemTypeDelRes = R.plurals.round_deleted
        actionModeCallback = ItemActionModeCallback(this, selector,
                R.plurals.round_selected)
        actionModeCallback?.setEditCallback { this.onEdit(it) }
        actionModeCallback?.setStatisticsCallback { this.onStatistics(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(updateReceiver,
                IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(updateReceiver)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
                DividerItemDecoration(context!!, R.drawable.full_divider))
        adapter = RoundAdapter()
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        binding.recyclerView.adapter = adapter

        // Get training
        trainingId = arguments!!.getLong(EditableListFragmentBase.ITEM_ID)

        binding.fab.visibility = View.GONE
        binding.fab.setOnClickListener {
            // New round to free training
            navigationController.navigateToCreateRound(training!!, binding.fab)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)
    }

    override fun onLoad(args: Bundle?): LoaderUICallback {
        training = TrainingDAO.loadTraining(trainingId)
        val rounds = TrainingDAO.loadRounds(trainingId)
        return {
            // Hide fab for standard rounds
            val supportsDeletion = training!!.standardRoundId == null
            if (supportsDeletion) {
                actionModeCallback?.setDeleteCallback { this.onDelete(it) }
            } else {
                actionModeCallback?.setDeleteCallback(null)
            }
            binding.fab.visibility = if (supportsDeletion) View.VISIBLE else View.GONE

            // Set round info
            val colorDrawable = if (training!!.environment.indoor) {
                R.drawable.ic_house_24dp
            } else {
                training!!.environment.weather.colorDrawable
            }
            binding.weatherIcon.setImageResource(colorDrawable)
            binding.detailRoundInfo.text = HtmlUtils.getTrainingInfoHTML(context!!, training!!, rounds, equals)
            adapter!!.setList(rounds)

            activity!!.invalidateOptionsMenu()

            ToolbarUtils.setTitle(this@TrainingFragment, training!!.title)
            ToolbarUtils.setSubtitle(this@TrainingFragment, training!!.formattedDate)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_scoreboard).isVisible = training != null
        menu.findItem(R.id.action_statistics).isVisible = training != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scoreboard -> {
                navigationController.navigateToScoreboard(trainingId)
                return true
            }
            R.id.action_statistics -> {
                navigationController.navigateToStatistics(TrainingDAO.loadRounds(training!!.id).map { it.id })
                return true
            }
            R.id.action_comment -> {
                MaterialDialog.Builder(context!!)
                        .title(R.string.comment)
                        .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("", training!!.comment) { _, input ->
                            training!!.comment = input.toString()
                            TrainingDAO.saveTraining(training!!)
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
        navigationController.navigateToEditRound(training!!, itemId)
    }

    private fun onStatistics(ids: List<Long>) {
        navigationController.navigateToStatistics(ids)
    }

    override fun deleteItem(item: Round): () -> Round {
        val round = RoundDAO.loadAugmentedRound(item)
        RoundDAO.deleteRound(item)
        return {
            RoundDAO.insertRound(round)
            item
        }
    }

    private inner class RoundAdapter : SimpleListAdapterBase<Round>(compareBy(Round::index)) {

        override fun onCreateViewHolder(parent: ViewGroup): SelectableViewHolder<Round> {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_round, parent, false)
            return ViewHolder(itemView)
        }
    }

    private inner class ViewHolder internal constructor(itemView: View) : SelectableViewHolder<Round>(itemView, selector, this@TrainingFragment, this@TrainingFragment) {
        private val binding: ItemRoundBinding = DataBindingUtil.bind(itemView)

        override fun bindItem(item: Round) {
            binding.title.text = resources.getQuantityString(R.plurals.rounds, item
                    .index + 1, item.index + 1)
            binding.subtitle.text = HtmlUtils.getRoundInfo(item, equals)
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
