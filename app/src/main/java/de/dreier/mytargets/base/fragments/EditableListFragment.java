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

package de.dreier.mytargets.base.fragments;

import android.os.Parcelable;

import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;

public abstract class EditableListFragment<T extends IIdSettable & IRecursiveModel & Comparable<T> & Parcelable> extends EditableListFragmentBase<T, SimpleListAdapterBase<T>> {

    protected final void onSelected(T item) {
        if (listener == null) {
            onItemSelected(item);
        } else {
            listener.onItemSelected(item);
            finish();
        }
    }

    protected abstract void onItemSelected(T item);
}
