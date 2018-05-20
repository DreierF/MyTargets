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

package de.dreier.mytargets.base.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

abstract class SimpleFragmentActivityBase : ChildActivityBase() {

    val childFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)

    protected abstract fun instantiateFragment(): Fragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            var childFragment: Fragment? = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (childFragment == null) {
                childFragment = instantiateFragment()
                childFragment.arguments = intent?.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, childFragment, FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        var childFragment: Fragment? = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (childFragment == null && intent?.extras != null) {
            childFragment = instantiateFragment()
            childFragment.arguments = intent.extras
        }

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, childFragment, FRAGMENT_TAG)
            .commit()
    }

    companion object {
        const val FRAGMENT_TAG = "fragment"
    }
}
