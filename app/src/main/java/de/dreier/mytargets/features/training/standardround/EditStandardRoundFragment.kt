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
package de.dreier.mytargets.features.training.standardround

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ItemSelectActivity
import de.dreier.mytargets.base.activities.ItemSelectActivity.Companion.ITEM
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder
import de.dreier.mytargets.base.fragments.EditFragmentBase
import de.dreier.mytargets.databinding.FragmentEditStandardRoundBinding
import de.dreier.mytargets.databinding.ItemRoundTemplateBinding
import de.dreier.mytargets.features.distance.DistanceActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.target.TargetActivity
import de.dreier.mytargets.features.training.target.TargetListFragment
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.utils.StandardRoundFactory
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.transitions.FabTransform
import de.dreier.mytargets.views.selector.DistanceSelector
import de.dreier.mytargets.views.selector.SelectorBase
import de.dreier.mytargets.views.selector.TargetSelector

class EditStandardRoundFragment : EditFragmentBase() {

    @State
    var standardRound: StandardRound? = null
    private var adapter: RoundTemplateAdapter? = null
    private lateinit var binding: FragmentEditStandardRoundBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_standard_round, container, false)

        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showUpAsX(this)
        setHasOptionsMenu(true)

        if (savedInstanceState == null) {
            if (arguments != null) {
                standardRound = arguments!!.getParcelable(ITEM)
            }
            if (standardRound == null) {
                standardRound = StandardRound()
                ToolbarUtils.setTitle(this, R.string.new_round_template)
                binding.name.setText(R.string.custom_round)
                // Initialize with default values
                addDefaultRound()
            } else {
                ToolbarUtils.setTitle(this, R.string.edit_standard_round)
                // Load saved values
                if (standardRound!!.club == StandardRoundFactory.CUSTOM) {
                    binding.name.setText(standardRound!!.name)
                } else {
                    standardRound!!.id = 0L
                    binding.name.setText(
                            String.format("%s %s", getString(R.string.custom), standardRound!!
                                    .name))
                    // When copying an existing standard round make sure
                    // we don't overwrite the other rounds templates
                    for (round in standardRound!!.loadRounds()) {
                        round.id = 0L
                    }
                }
            }
        }

        adapter = RoundTemplateAdapter(this, standardRound!!.loadRounds())
        binding.rounds.adapter = adapter
        binding.addButton.setOnClickListener { onAddRound() }
        binding.deleteStandardRound.setOnClickListener { onDeleteStandardRound() }

        return binding.root
    }

    private fun addDefaultRound() {
        val round = RoundTemplate()
        round.shotsPerEnd = SettingsManager.shotsPerEnd
        round.endCount = SettingsManager.endCount
        round.targetTemplate = SettingsManager.target
        round.distance = SettingsManager.distance
        standardRound!!.loadRounds().add(round)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        FabTransform.setup(activity!!, binding.root)
    }

    private fun onAddRound() {
        val newItemIndex = standardRound!!.loadRounds().size
        if (newItemIndex > 0) {
            val r = standardRound!!.loadRounds()[newItemIndex - 1]
            val roundTemplate = RoundTemplate()
            roundTemplate.endCount = r.endCount
            roundTemplate.shotsPerEnd = r.shotsPerEnd
            roundTemplate.distance = r.distance
            roundTemplate.targetTemplate = r.targetTemplate
            standardRound!!.loadRounds().add(roundTemplate)
        } else {
            addDefaultRound()
        }
        adapter!!.notifyItemInserted(newItemIndex)
    }

    private fun onDeleteStandardRound() {
        standardRound!!.delete()
        activity!!.setResult(RESULT_STANDARD_ROUND_DELETED, null)
        finish()
    }

    override fun onSave() {
        standardRound!!.club = StandardRoundFactory.CUSTOM
        standardRound!!.name = binding.name.text.toString()
        standardRound!!.save()

        val round = standardRound!!.loadRounds()[0] //FIXME how is this possible?
        SettingsManager.shotsPerEnd = round.shotsPerEnd
        SettingsManager.endCount = round.endCount
        SettingsManager.target = round.targetTemplate
        SettingsManager.distance = round.distance

        val data = Intent()
        data.putExtra(ITEM, standardRound)
        activity!!.setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val intentData = data.getBundleExtra(ItemSelectActivity.INTENT)
            val index = intentData.getInt(SelectorBase.INDEX)
            when (requestCode) {
                DistanceSelector.DISTANCE_REQUEST_CODE -> {
                    standardRound!!.loadRounds()[index].distance = data.getParcelableExtra(ITEM)
                    adapter!!.notifyItemChanged(index)
                }
                TargetSelector.TARGET_REQUEST_CODE -> {
                    standardRound!!.loadRounds()[index]
                            .targetTemplate = data.getParcelableExtra(ITEM)
                    adapter!!.notifyItemChanged(index)
                }
            }
        }
    }

    private inner class RoundTemplateHolder internal constructor(view: View) : DynamicItemHolder<RoundTemplate>(view) {

        internal var binding: ItemRoundTemplateBinding = DataBindingUtil.bind(view)

        override fun onBind(item: RoundTemplate, position: Int, fragment: Fragment, removeListener: View.OnClickListener) {
            this.item = item

            // Set title of round
            binding.roundNumber.text = fragment.resources
                    .getQuantityString(R.plurals.rounds, position + 1, position + 1)
            item.index = position

            binding.distance.setOnClickListener { selectedItem, index ->
                IntentWrapper(DistanceActivity::class.java)
                        .with(ItemSelectActivity.ITEM, selectedItem!!)
                        .with(SelectorBase.INDEX, index)
                        .withContext(this@EditStandardRoundFragment)
                        .forResult(DistanceSelector.DISTANCE_REQUEST_CODE)
                        .start()
            }
            binding.distance.setItemIndex(position)
            binding.distance.setItem(item.distance)

            // Target round
            binding.target.setOnClickListener { selectedItem, index ->
                IntentWrapper(TargetActivity::class.java)
                        .with(ItemSelectActivity.ITEM, selectedItem!!)
                        .with(SelectorBase.INDEX, index)
                        .with(TargetListFragment.FIXED_TYPE, TargetListFragment.EFixedType.NONE.name)
                        .withContext(this@EditStandardRoundFragment)
                        .forResult(TargetSelector.TARGET_REQUEST_CODE)
                        .start()
            }
            binding.target.setItemIndex(position)
            binding.target.setItem(item.targetTemplate)

            // Ends
            binding.endCount.setTextPattern(R.plurals.passe)
            binding.endCount.setOnValueChangedListener { item.endCount = it }
            binding.endCount.value = item.endCount

            // Shots per end
            binding.shotCount.setTextPattern(R.plurals.arrow)
            binding.shotCount.minimum = 1
            binding.shotCount.maximum = 12
            binding.shotCount.setOnValueChangedListener { item.shotsPerEnd = it }
            binding.shotCount.value = item.shotsPerEnd

            if (position == 0) {
                binding.remove.visibility = View.GONE
            } else {
                binding.remove.visibility = View.VISIBLE
                binding.remove.setOnClickListener(removeListener)
            }
        }
    }

    private inner class RoundTemplateAdapter internal constructor(fragment: Fragment, list: List<RoundTemplate>) : DynamicItemAdapter<RoundTemplate>(fragment, list.toMutableList(), R.string.round_removed) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicItemHolder<RoundTemplate> {
            val v = inflater.inflate(R.layout.item_round_template, parent, false)
            return RoundTemplateHolder(v)
        }
    }

    companion object {

        val RESULT_STANDARD_ROUND_DELETED = Activity.RESULT_FIRST_USER

        fun createIntent(): IntentWrapper {
            return IntentWrapper(EditStandardRoundActivity::class.java)
        }

        fun editIntent(item: StandardRound): IntentWrapper {
            return IntentWrapper(EditStandardRoundActivity::class.java)
                    .with(ITEM, item)
        }
    }
}
