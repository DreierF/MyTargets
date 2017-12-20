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

import android.os.Parcelable
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.IRecursiveModel

abstract class EditableListFragment<T> : EditableListFragmentBase<T, SimpleListAdapterBase<T>>() where T : IIdSettable, T : IRecursiveModel, T : Parcelable, T : Comparable<T> {

    override fun onSelected(item: T) {
        if (listener == null) {
            onItemSelected(item)
        } else {
            listener.onItemSelected(item)
            finish()
        }
    }

    protected abstract fun onItemSelected(item: T)
}
