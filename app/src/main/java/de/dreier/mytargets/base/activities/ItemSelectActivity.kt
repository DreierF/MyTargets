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

import android.app.Activity
import android.content.Intent
import android.os.Parcelable

import de.dreier.mytargets.base.fragments.ListFragmentBase

abstract class ItemSelectActivity : SimpleFragmentActivityBase(), ListFragmentBase.OnItemSelectedListener {

    override fun onItemSelected(item: Parcelable) {
        val data = Intent()
        data.putExtra(ITEM, item)
        data.putExtra(INTENT, if (intent != null) intent.extras else null)
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        const val ITEM = "item"
        const val INTENT = "intent"
    }

}
