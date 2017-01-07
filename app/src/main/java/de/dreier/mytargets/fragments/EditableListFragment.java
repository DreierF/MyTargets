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

package de.dreier.mytargets.fragments;

import com.raizlabs.android.dbflow.structure.Model;

import org.parceler.Parcels;

import de.dreier.mytargets.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class EditableListFragment<T extends IIdSettable & Model & Comparable<T>> extends EditableListFragmentBase<T> {

    protected SimpleListAdapterBase<T> mAdapter;

    protected final void onSelected(T item) {
        if (listener == null) {
            onItemSelected(item);
        } else {
            listener.onItemSelected(Parcels.wrap(item));
            finish();
        }
    }

    protected abstract void onItemSelected(T item);

    @Override
    protected ItemAdapter<T> getAdapter() {
        return mAdapter;
    }
}
