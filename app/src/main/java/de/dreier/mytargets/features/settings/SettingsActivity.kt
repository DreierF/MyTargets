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

package de.dreier.mytargets.features.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceFragmentCompat.ARG_PREFERENCE_ROOT
import androidx.preference.PreferenceScreen
import android.view.MenuItem
import androidx.fragment.app.transaction
import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase
import de.dreier.mytargets.features.settings.ESettingsScreens.MAIN

class SettingsActivity : SimpleFragmentActivityBase(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public override fun instantiateFragment(): Fragment {
        val key = intent.getStringExtra(ARG_PREFERENCE_ROOT)
        return if (key != null) {
            ESettingsScreens.from(key).create()
        } else MAIN.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            val manager = supportFragmentManager
            if (manager != null) {
                val currFrag = manager
                    .findFragmentById(android.R.id.content) as SettingsFragmentBase
                currFrag.onFragmentResume()
            }
        }
    }

    override fun onPreferenceStartScreen(
        preferenceFragmentCompat: PreferenceFragmentCompat,
        preferenceScreen: PreferenceScreen
    ): Boolean {
        val screen = ESettingsScreens.from(preferenceScreen.key)
        val fragment = screen.create()
        val args = Bundle()
        args.putString(ARG_PREFERENCE_ROOT, preferenceScreen.key)
        fragment.arguments = args
        supportFragmentManager.transaction {
            add(android.R.id.content, fragment, preferenceScreen.key)
            addToBackStack(preferenceScreen.key)
        }
        return true
    }

    override fun onBackPressed() {
        navigationController.finish(animate = false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (childFragment is MainSettingsFragment) {
                    navigationController.finish(animate = false)
                } else {
                    intent.putExtra(ARG_PREFERENCE_ROOT, MAIN.key)
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(
                        android.R.id.content,
                        instantiateFragment(),
                        SimpleFragmentActivityBase.FRAGMENT_TAG
                    )
                    ft.commit()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
