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
import android.util.AttributeSet
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.SelectorItemImageDetailsBinding
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound

class StandardRoundSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SelectorBase<AugmentedStandardRound, SelectorItemImageDetailsBinding>(
    context,
    attrs,
    R.layout.selector_item_image_details,
    STANDARD_ROUND_REQUEST_CODE
) {

    private val standardRoundDAO = ApplicationInstance.db.standardRoundDAO()

    override fun bindView(item: AugmentedStandardRound) {
        view.name.text = item.standardRound.name
        view.details.visibility = View.VISIBLE
        view.details.text = item.getDescription(context)
        view.image.setImageDrawable(item.targetDrawable)
    }

    fun setItemId(standardRoundId: Long?) {
        var standardRound = standardRoundDAO.loadStandardRoundOrNull(standardRoundId!!)
        // If the round has been removed, choose default one
        if (standardRound == null || standardRoundDAO.loadRoundTemplates(standardRound.id).isEmpty()) {
            standardRound = standardRoundDAO.loadStandardRound(32L)
        }
        setItem(
            AugmentedStandardRound(
                standardRound,
                standardRoundDAO.loadRoundTemplates(standardRound.id).toMutableList()
            )
        )
    }

    companion object {
        const val STANDARD_ROUND_REQUEST_CODE = 10
    }
}
