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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.util.AttributeSet

import de.dreier.mytargets.features.training.standardround.StandardRoundActivity
import de.dreier.mytargets.shared.models.db.StandardRound

class StandardRoundSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ImageSelectorBase<StandardRound>(context, attrs) {

    init {
        defaultActivity = StandardRoundActivity::class.java
        requestCode = STANDARD_ROUND_REQUEST_CODE
    }

    fun setItemId(standardRoundId: Long?) {
        var standardRound = StandardRound[standardRoundId!!]
        // If the round has been removed, choose default one
        if (standardRound == null || standardRound.loadRounds().isEmpty()) {
            standardRound = StandardRound[32L]
        }
        setItem(standardRound)
    }

    companion object {
        private val STANDARD_ROUND_REQUEST_CODE = 10
    }
}
