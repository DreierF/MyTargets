/*
 * Copyright (C) 2016 Florian Dreier
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

import android.content.Context;

import org.parceler.Parcels;

import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class EditableListFragment<T extends IIdSettable & Comparable<T>> extends EditableListFragmentBase<T> {

    protected ListAdapterBase<T> mAdapter;
    private OnItemSelectedListener listener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        if (getParentFragment() instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) getParentFragment();
        }
    }

    protected final void onSelected(T item) {
        if (listener == null) {
            onItemSelected(item);
        } else {
            listener.onItemSelected(Parcels.wrap(item));
        }
    }

    protected abstract void onItemSelected(T item);

    @Override
    protected ItemAdapter<T> getAdapter() {
        return mAdapter;
    }
}
