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
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder
import de.dreier.mytargets.base.db.StandardRoundFactory
import de.dreier.mytargets.base.fragments.EditFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.INTENT
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.databinding.FragmentEditStandardRoundBinding
import de.dreier.mytargets.databinding.ItemRoundTemplateBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.views.selector.DistanceSelector
import de.dreier.mytargets.views.selector.SelectorBase
import de.dreier.mytargets.views.selector.TargetSelector
import timber.log.Timber

class EditStandardRoundFragment : EditFragmentBase() {

    private val standardRoundDAO = ApplicationInstance.db.standardRoundDAO()

    @State
    var standardRound: AugmentedStandardRound? = null
    private var adapter: RoundTemplateAdapter? = null
    private lateinit var binding: FragmentEditStandardRoundBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                ToolbarUtils.setTitle(this, R.string.new_round_template)
                binding.name.setText(R.string.custom_round)
                standardRound = AugmentedStandardRound(
                    StandardRound(),
                    mutableListOf(getDefaultRoundTemplate())
                )
            } else {
                ToolbarUtils.setTitle(this, R.string.edit_standard_round)
                // Load saved values
                if (standardRound!!.standardRound.club == StandardRoundFactory.CUSTOM) {
                    binding.name.setText(standardRound!!.standardRound.name)
                } else {
                    standardRound!!.standardRound.id = 0L
                    binding.name.setText(
                        "%s %s".format(
                            getString(R.string.custom), standardRound!!.standardRound
                                .name
                        )
                    )
                    // When copying an existing standard round make sure
                    // we don't overwrite the other rounds templates
                    for (round in standardRound!!.roundTemplates) {
                        round.id = 0L
                    }
                }
            }
        }

        adapter = RoundTemplateAdapter(this, standardRound!!.roundTemplates)
        binding.rounds.adapter = adapter
        binding.addButton.setOnClickListener { onAddRound() }
        binding.deleteStandardRound.setOnClickListener { onDeleteStandardRound() }

        return binding.root
    }

    private fun getDefaultRoundTemplate(): RoundTemplate {
        val round = RoundTemplate()
        round.shotsPerEnd = SettingsManager.shotsPerEnd
        round.endCount = SettingsManager.endCount
        round.targetTemplate = SettingsManager.target
        round.distance = SettingsManager.distance
        return round
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Utils.setupFabTransform(activity!!, binding.root)
    }

    private fun onAddRound() {
        val newItemIndex = standardRound!!.roundTemplates.size
        if (newItemIndex > 0) {
            val r = standardRound!!.roundTemplates[newItemIndex - 1]
            val roundTemplate = RoundTemplate()
            roundTemplate.endCount = r.endCount
            roundTemplate.shotsPerEnd = r.shotsPerEnd
            roundTemplate.distance = r.distance
            roundTemplate.targetTemplate = r.targetTemplate
            standardRound!!.roundTemplates.add(roundTemplate)
        } else {
            Timber.w("This should never get executed") //TODO remove else part if no reports occur
            standardRound!!.roundTemplates.add(getDefaultRoundTemplate())
        }
        adapter!!.notifyItemInserted(newItemIndex)
    }

    private fun onDeleteStandardRound() {
        standardRoundDAO.deleteStandardRound(standardRound!!.standardRound)
        navigationController.setResult(RESULT_STANDARD_ROUND_DELETED)
        navigationController.finish()
    }

    override fun onSave() {
        standardRound!!.standardRound.club = StandardRoundFactory.CUSTOM
        standardRound!!.standardRound.name = binding.name.text.toString()
        standardRoundDAO.saveStandardRound(
            standardRound!!.standardRound,
            standardRound!!.roundTemplates
        )

        val round = standardRound!!.roundTemplates[0]
        SettingsManager.shotsPerEnd = round.shotsPerEnd
        SettingsManager.endCount = round.endCount
        SettingsManager.target = round.targetTemplate
        SettingsManager.distance = round.distance

        navigationController.setResultSuccess(standardRound!!)
        navigationController.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val intentData = data.getBundleExtra(INTENT)
            val index = intentData.getInt(SelectorBase.INDEX)
            when (requestCode) {
                DistanceSelector.DISTANCE_REQUEST_CODE -> {
                    standardRound!!.roundTemplates[index].distance = data.getParcelableExtra(ITEM)
                    adapter!!.notifyItemChanged(index)
                }
                TargetSelector.TARGET_REQUEST_CODE -> {
                    standardRound!!.roundTemplates[index]
                        .targetTemplate = data.getParcelableExtra(ITEM)
                    adapter!!.notifyItemChanged(index)
                }
            }
        }
    }

    private inner class RoundTemplateHolder internal constructor(view: View) :
        DynamicItemHolder<RoundTemplate>(view) {

        internal var binding = ItemRoundTemplateBinding.bind(view)

        override fun onBind(
            item: RoundTemplate,
            position: Int,
            fragment: Fragment,
            removeListener: View.OnClickListener
        ) {
            this.item = item

            // Set title of round
            binding.roundNumber.text = fragment.resources
                .getQuantityString(R.plurals.rounds, position + 1, position + 1)
            item.index = position

            binding.distance.setOnClickListener { selectedItem, index ->
                navigationController.navigateToDistance(
                    selectedItem!!,
                    index,
                    DistanceSelector.DISTANCE_REQUEST_CODE
                )
            }
            binding.distance.itemIndex = position
            binding.distance.setItem(item.distance)

            // Target round
            binding.target.setOnClickListener { selectedItem, index ->
                navigationController.navigateToTarget(selectedItem!!, index)
            }
            binding.target.itemIndex = position
            binding.target.setItem(item.targetTemplate)

            // Ends
            binding.endCount.textPattern = R.plurals.passe
            binding.endCount.setOnValueChangedListener { item.endCount = it }
            binding.endCount.value = item.endCount

            // Shots per end
            binding.shotCount.textPattern = R.plurals.arrow
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

    private inner class RoundTemplateAdapter internal constructor(
        fragment: Fragment,
        list: MutableList<RoundTemplate>
    ) : DynamicItemAdapter<RoundTemplate>(fragment, list, R.string.round_removed) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DynamicItemHolder<RoundTemplate> {
            val v = inflater.inflate(R.layout.item_round_template, parent, false)
            return RoundTemplateHolder(v)
        }
    }

    companion object {
        const val RESULT_STANDARD_ROUND_DELETED = Activity.RESULT_FIRST_USER
    }
}
