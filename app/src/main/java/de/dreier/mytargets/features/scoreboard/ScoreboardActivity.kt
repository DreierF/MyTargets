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

package de.dreier.mytargets.features.scoreboard

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment

import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.ToolbarUtils

class ScoreboardActivity : SimpleFragmentActivityBase() {

    override fun instantiateFragment(): Fragment {
        return ScoreboardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ToolbarUtils.showHomeAsUp(this)
    }

    companion object {

        @VisibleForTesting
        val TRAINING_ID = "training_id"
        @VisibleForTesting
        val ROUND_ID = "round_id"

        @JvmOverloads
        fun getIntent(trainingId: Long, roundId: Long = -1): IntentWrapper {
            return IntentWrapper(ScoreboardActivity::class.java)
                    .with(TRAINING_ID, trainingId)
                    .with(ROUND_ID, roundId)
        }
    }
}
