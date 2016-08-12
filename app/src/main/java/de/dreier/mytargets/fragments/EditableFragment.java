/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;

import org.parceler.Parcels;

import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class EditableFragment<T extends IIdSettable> extends EditableFragmentBase<T> {

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
    protected T getItem(long id) {
        return mAdapter.getItemById(id);
    }

    @Override
    protected void addItem(T item) {
        mAdapter.add(item);
    }

    @Override
    protected void removeItem(T item) {
        mAdapter.remove(item);
    }
}
