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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.databinding.DataBindingUtil
import android.util.AttributeSet
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.base.db.dao.StandardRoundDAO
import de.dreier.mytargets.databinding.SelectorItemImageDetailsBinding
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound

class StandardRoundSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : SelectorBase<AugmentedStandardRound>(context, attrs, R.layout.selector_item_image_details, STANDARD_ROUND_REQUEST_CODE) {

    private lateinit var binding: SelectorItemImageDetailsBinding

    override fun bindView(item: AugmentedStandardRound) {
        binding = DataBindingUtil.bind(view)
        binding.name.text = item.standardRound.name
        binding.details.visibility = View.VISIBLE
        binding.details.text = item.getDescription(context)
        binding.image.setImageDrawable(item.targetDrawable)
    }

    fun setItemId(standardRoundId: Long?) {
        var standardRound = StandardRoundDAO.loadStandardRoundOrNull(standardRoundId!!)
        // If the round has been removed, choose default one
        if (standardRound == null || StandardRoundDAO.loadRoundTemplates(standardRound.id).isEmpty()) {
            standardRound = StandardRoundDAO.loadStandardRound(32L)
        }
        setItem(AugmentedStandardRound(standardRound, StandardRoundDAO.loadRoundTemplates(standardRound.id)))
    }

    companion object {
        const val STANDARD_ROUND_REQUEST_CODE = 10
    }
}
