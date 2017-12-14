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
package de.dreier.mytargets.features.training.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.DatePicker
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ItemSelectActivity
import de.dreier.mytargets.base.fragments.EditFragmentBase
import de.dreier.mytargets.base.fragments.EditableListFragmentBase.Companion.ITEM_ID
import de.dreier.mytargets.databinding.FragmentEditTrainingBinding
import de.dreier.mytargets.features.bows.EditBowFragment
import de.dreier.mytargets.features.distance.DistanceActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.ETrainingType
import de.dreier.mytargets.features.training.ETrainingType.FREE_TRAINING
import de.dreier.mytargets.features.training.ETrainingType.TRAINING_WITH_STANDARD_ROUND
import de.dreier.mytargets.features.training.RoundFragment
import de.dreier.mytargets.features.training.details.TrainingFragment
import de.dreier.mytargets.features.training.environment.EnvironmentActivity
import de.dreier.mytargets.features.training.input.InputActivity
import de.dreier.mytargets.features.training.standardround.StandardRoundActivity
import de.dreier.mytargets.features.training.target.TargetActivity
import de.dreier.mytargets.features.training.target.TargetListFragment
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.targets.models.WA3Ring3Spot
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.transitions.FabTransform
import de.dreier.mytargets.views.selector.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

class EditTrainingFragment : EditFragmentBase(), DatePickerDialog.OnDateSetListener {

    private var trainingId: Long? = null
    private var trainingType = FREE_TRAINING
    private var date: LocalDate? = LocalDate.now()
    private lateinit var binding: FragmentEditTrainingBinding
    private var roundTarget: Target? = null

    private val training: Training
        get() {
            val training = if (trainingId == null) {
                Training()
            } else {
                Training[trainingId!!]!!
            }
            training.title = binding.training.text.toString()
            training.date = date
            training.environment = binding.environment.selectedItem!!
            training.bowId = if (binding.bow.selectedItem == null)
                null
            else
                binding.bow
                        .selectedItem!!
                        .id
            training.arrowId = if (binding.arrow.selectedItem == null)
                null
            else
                binding.arrow
                        .selectedItem!!.id
            training.arrowNumbering = binding.numberArrows.isChecked

            SettingsManager.bow = training.bowId
            SettingsManager.arrow = training.arrowId
            SettingsManager.arrowNumbersEnabled = training.arrowNumbering
            SettingsManager.indoor = training.indoor
            return training
        }

    private val round: Round
        get() {
            val round = Round()
            round.target = binding.target.selectedItem!!
            round.shotsPerEnd = binding.arrows.progress
            round.maxEndCount = null
            round.distance = binding.distance.selectedItem!!

            SettingsManager.target = binding.target.selectedItem!!
            SettingsManager.distance = round.distance
            SettingsManager.shotsPerEnd = round.shotsPerEnd
            return round
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_training, container, false)

        val arguments = arguments
        if (arguments != null && arguments.containsKey(ITEM_ID)) {
            trainingId = arguments.getLong(ITEM_ID)
        }
        trainingType = if (activity?.intent?.action == CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION) {
            TRAINING_WITH_STANDARD_ROUND
        } else {
            FREE_TRAINING
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
        binding.target.setOnClickListener { selectedItem, index ->
            IntentWrapper(TargetActivity::class.java)
                    .with(ItemSelectActivity.ITEM, selectedItem!!)
                    .with(SelectorBase.INDEX, index)
                    .with(TargetListFragment.FIXED_TYPE, TargetListFragment.EFixedType.NONE.name)
                    .withContext(this)
                    .forResult(TargetSelector.TARGET_REQUEST_CODE)
                    .start()
        }
        binding.distance.setOnClickListener { selectedItem, index ->
            IntentWrapper(DistanceActivity::class.java)
                    .with(ItemSelectActivity.ITEM, selectedItem!!)
                    .with(SelectorBase.INDEX, index)
                    .withContext(this)
                    .forResult(DistanceSelector.DISTANCE_REQUEST_CODE)
                    .start()
        }

        binding.standardRound.setOnClickListener { selectedItem, index ->
            IntentWrapper(StandardRoundActivity::class.java)
                    .with(ItemSelectActivity.ITEM, selectedItem!!)
                    .with(SelectorBase.INDEX, index)
                    .withContext(this)
                    .forResult(StandardRoundSelector.STANDARD_ROUND_REQUEST_CODE)
                    .start()
        }
        binding.standardRound.setOnUpdateListener { item -> roundTarget = item!!.loadRounds()[0].targetTemplate }
        binding.changeTargetFace.setOnClickListener {
            TargetListFragment.getIntent(roundTarget!!)
                    .withContext(this)
                    .forResult(SR_TARGET_REQUEST_CODE)
                    .start()
        }
        binding.arrow.setOnAddClickListener {
            navigationController.navigateToCreateArrow()
                    .forResult(ArrowSelector.ARROW_ADD_REQUEST_CODE)
                    .start()
        }
        binding.bow.setOnAddClickListener {
            EditBowFragment.createIntent(EBowType.RECURVE_BOW)
                    .forResult(BowSelector.BOW_ADD_REQUEST_CODE)
        }
        binding.bow.setOnUpdateListener { this.setScoringStyleForCompoundBow(it) }
        binding.environment.setOnClickListener { selectedItem, index ->
            IntentWrapper(EnvironmentActivity::class.java)
                    .with(ItemSelectActivity.ITEM, selectedItem!!)
                    .with(SelectorBase.INDEX, index)
                    .forResult(EnvironmentSelector.ENVIRONMENT_REQUEST_CODE)
                    .start()
        }
        binding.trainingDate.setOnClickListener { onDateClick() }

        if (trainingId == null) {
            ToolbarUtils.setTitle(this, R.string.new_training)
            binding.training.setText(getString(
                    if (trainingType == ETrainingType.COMPETITION)
                        R.string.competition
                    else
                        R.string.training))
            setTrainingDate()
            loadRoundDefaultValues()
            binding.bow.setItemId(SettingsManager.bow)
            binding.arrow.setItemId(SettingsManager.arrow)
            binding.standardRound.setItemId(SettingsManager.standardRound)
            binding.numberArrows.isChecked = SettingsManager.arrowNumbersEnabled
            if (savedInstanceState == null) {
                binding.environment.queryWeather(this, REQUEST_LOCATION_PERMISSION)
            }
            binding.changeTargetFace.visibility = if (trainingType == TRAINING_WITH_STANDARD_ROUND)
                VISIBLE
            else
                GONE
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_training)
            val train = Training[trainingId!!]!!
            binding.training.setText(train.title)
            date = train.date
            binding.bow.setItemId(train.bowId)
            binding.arrow.setItemId(train.arrowId)
            binding.environment.setItem(train.environment)
            setTrainingDate()
            binding.notEditable.visibility = GONE
            binding.changeTargetFace.visibility = if (train.standardRoundId != null) VISIBLE else GONE
        }
        applyTrainingType()
        updateArrowsLabel()

        return binding.root
    }

    private fun updateArrowsLabel() {
        binding.arrowsLabel.text = resources
                .getQuantityString(R.plurals.arrow, binding.arrows.progress, binding.arrows.progress)
    }

    private fun setScoringStyleForCompoundBow(bow: Bow?) {
        val target = binding.target.selectedItem
        if (bow != null && target != null && target.id <= WA3Ring3Spot.ID) {
            if (bow.type === EBowType.COMPOUND_BOW && target.scoringStyleIndex == 0) {
                target.scoringStyleIndex = 2
                binding.target.setItem(target)
            } else if (bow.type !== EBowType.COMPOUND_BOW && target.scoringStyleIndex == 2) {
                target.scoringStyleIndex = 0
                binding.target.setItem(target)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        FabTransform.setup(activity!!, binding.root)
    }

    private fun applyTrainingType() {
        val `in`: View
        val out: View
        if (trainingType == FREE_TRAINING) {
            `in` = binding.practiceLayout
            out = binding.standardRound
        } else {
            out = binding.practiceLayout
            `in` = binding.standardRound
        }
        `in`.visibility = VISIBLE
        out.visibility = GONE
    }

    private fun onDateClick() {
        val datePickerDialog = DatePickerFragment.newInstance(date!!)
        datePickerDialog.setTargetFragment(this, REQ_SELECTED_DATE)
        datePickerDialog.show(activity!!.supportFragmentManager, "date_picker")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            binding.environment.onPermissionResult(activity!!, grantResults)
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        setTrainingDate()
    }

    private fun setTrainingDate() {
        binding.trainingDate.text = date!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    override fun onSave() {
        val training = AugmentedTraining(training)
        finish()

        if (trainingId == null) {
            if (trainingType == FREE_TRAINING) {
                training.training.standardRoundId = null
                training.rounds = ArrayList()
                training.rounds.add(AugmentedRound(round))
            } else {
                val standardRound = binding.standardRound.selectedItem
                SettingsManager.standardRound = standardRound!!.id
                if (standardRound.id == 0L) {
                    standardRound.save()
                }
                training.training.standardRoundId = standardRound.id
                training.initRoundsFromTemplate(standardRound)
                for (round in training.rounds) {
                    round.round.target = roundTarget!!
                }
            }
            training.toTraining().save()

            val round = training.rounds[0]

            TrainingFragment.getIntent(training.training)
                    .withContext(this)
                    .noAnimation()
                    .start()
            RoundFragment.getIntent(round.round)
                    .withContext(this)
                    .noAnimation()
                    .start()
            InputActivity.createIntent(round.round)
                    .withContext(this)
                    .start()
        } else {
            // Edit training
            training.toTraining().update()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.target.onActivityResult(requestCode, resultCode, data)
        binding.distance.onActivityResult(requestCode, resultCode, data)
        binding.standardRound.onActivityResult(requestCode, resultCode, data)
        binding.arrow.onActivityResult(requestCode, resultCode, data)
        binding.bow.onActivityResult(requestCode, resultCode, data)
        binding.environment.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SR_TARGET_REQUEST_CODE && data != null) {
            val target = data.getParcelableExtra<Target>(ItemSelectActivity.ITEM)
            val item = binding.standardRound.selectedItem
            item!!.loadRounds().forEach { it.targetTemplate = target }
            binding.standardRound.setItem(item)
        }
    }

    private fun loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.distance)
        binding.arrows.progress = SettingsManager.shotsPerEnd
        binding.target.setItem(SettingsManager.target)
    }

    companion object {
        const val CREATE_FREE_TRAINING_ACTION = "free_training"
        const val CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION = "with_standard_round"

        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQ_SELECTED_DATE = 2
        private const val SR_TARGET_REQUEST_CODE = 11

        fun createIntent(trainingTypeAction: String): IntentWrapper {
            return IntentWrapper(EditTrainingActivity::class.java)
                    .action(trainingTypeAction)
        }

        fun editIntent(trainingId: Long): IntentWrapper {
            return IntentWrapper(EditTrainingActivity::class.java)
                    .with(ITEM_ID, trainingId)
        }
    }
}
