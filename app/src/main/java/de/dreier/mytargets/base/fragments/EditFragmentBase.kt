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
package de.dreier.mytargets.base.fragments

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import de.dreier.mytargets.R

abstract class EditFragmentBase : FragmentBase() {

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            activity!!.setResult(Activity.RESULT_OK)
            onSave()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun onSave()
}
