/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import org.parceler.Parcels;

import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class EditableListFragment<T extends IIdSettable & Comparable<T>> extends EditableListFragmentBase<T> {

    protected ListAdapterBase<T> mAdapter;

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
