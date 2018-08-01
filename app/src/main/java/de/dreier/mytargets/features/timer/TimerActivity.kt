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

package de.dreier.mytargets.features.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.utils.Utils

class TimerActivity : ChildActivityBase() {
    private var childFragment: Fragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            childFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (childFragment == null) {
                childFragment = instantiateFragment()
                childFragment?.arguments = intent?.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, childFragment!!, FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setShowWhenLocked(this, SettingsManager.timerKeepAboveLockscreen)
    }

    fun instantiateFragment(): Fragment {
        return TimerFragment()
    }

    companion object {
        private const val FRAGMENT_TAG = "fragment"
    }

}
