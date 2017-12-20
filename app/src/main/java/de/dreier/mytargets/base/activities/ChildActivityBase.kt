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

package de.dreier.mytargets.base.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem

import com.google.firebase.analytics.FirebaseAnalytics

import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.utils.Utils
import im.delight.android.languages.Language

abstract class ChildActivityBase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        logEvent(javaClass.simpleName)
    }

    private fun logEvent(event: String) {
        FirebaseAnalytics.getInstance(this).logEvent(event, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate()) {
            if (Utils.isLollipop) {
                finishAfterTransition()
            } else {
                finish()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
            }
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
