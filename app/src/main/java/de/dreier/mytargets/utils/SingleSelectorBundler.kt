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

package de.dreier.mytargets.utils

import android.os.Bundle

import com.evernote.android.state.Bundler

import de.dreier.mytargets.utils.multiselector.SingleSelector

class SingleSelectorBundler : Bundler<SingleSelector> {
    override fun put(key: String, value: SingleSelector, bundle: Bundle) {
        bundle.putBundle(key, value.saveSelectionStates())
    }

    override fun get(key: String, bundle: Bundle): SingleSelector {
        val selector = SingleSelector()
        selector.restoreSelectionStates(bundle.getBundle(key)!!)
        return selector
    }
}
