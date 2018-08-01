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

package de.dreier.mytargets.features.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase
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
        const val TRAINING_ID = "training_id"
        const val ROUND_ID = "round_id"
    }
}
