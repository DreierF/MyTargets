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
package de.dreier.mytargets.features.rounds

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditFragmentBase
import de.dreier.mytargets.base.fragments.EditableListFragmentBase.Companion.ITEM_ID
import de.dreier.mytargets.databinding.FragmentEditRoundBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.EditRoundActivity
import de.dreier.mytargets.features.training.RoundFragment
import de.dreier.mytargets.features.training.input.InputActivity
import de.dreier.mytargets.features.training.target.TargetListFragment
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.transitions.FabTransform
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

class EditRoundFragment : EditFragmentBase() {
    private var trainingId: Long = 0
    private var roundId: Long? = null
    private lateinit var binding: FragmentEditRoundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_round, container, false)

        trainingId = arguments!!.getLong(ITEM_ID)
        if (arguments!!.containsKey(ROUND_ID)) {
            roundId = arguments!!.getLong(ROUND_ID)
        }

        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showUpAsX(this)
        setHasOptionsMenu(true)

        binding.arrows.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                updateArrowsLabel()
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {}
        })
        binding.target.setOnActivityResultContext(this)
        binding.distance.setOnActivityResultContext(this)

        if (roundId == null) {
            ToolbarUtils.setTitle(this, R.string.new_round)
            loadRoundDefaultValues()
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_round)
            val round = Round[roundId!!]
            binding.distance.setItem(round!!.distance)
            binding.target.setItem(round.target)
            binding.target.setFixedType(TargetListFragment.EFixedType.TARGET)
            binding.notEditable.visibility = View.GONE
            if (round.training.standardRoundId != null) {
                binding.distanceLayout.visibility = View.GONE
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        FabTransform.setup(activity!!, binding.root)
    }

    override fun onSave() {
        finish()
        if (roundId == null) {
            val round = onSaveRound()
            RoundFragment.getIntent(round!!)
                    .withContext(this)
                    .noAnimation()
                    .start()
            InputActivity.createIntent(round)
                    .withContext(this)
                    .start()
        } else {
            onSaveRound()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    private fun onSaveRound(): Round? {
        val training = Training[trainingId]

        val round: Round?
        if (roundId == null) {
            round = Round()
            round.trainingId = trainingId
            round.shotsPerEnd = binding.arrows.progress
            round.maxEndCount = null
            round.index = training!!.loadRounds().size
        } else {
            round = Round[roundId!!]
        }
        round!!.distance = binding.distance.selectedItem!!
        round.target = binding.target.selectedItem!!
        round.save()
        return round
    }

    private fun updateArrowsLabel() {
        binding.arrowsLabel.text = resources
                .getQuantityString(R.plurals.arrow, binding.arrows.progress,
                        binding.arrows.progress)
    }

    private fun loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.distance)
        binding.arrows.progress = SettingsManager.shotsPerEnd
        binding.target.setItem(SettingsManager.target)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.target.onActivityResult(requestCode, resultCode, data)
        binding.distance.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val ROUND_ID = "round_id"

        fun createIntent(training: Training): IntentWrapper {
            return IntentWrapper(EditRoundActivity::class.java)
                    .with(ITEM_ID, training.id)
        }

        fun editIntent(training: Training, roundId: Long): IntentWrapper {
            return IntentWrapper(EditRoundActivity::class.java)
                    .with(ITEM_ID, training.id)
                    .with(ROUND_ID, roundId)
        }
    }
}
