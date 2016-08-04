/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;

import com.raizlabs.android.dbflow.structure.Model;

import org.parceler.Parcels;

import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class EditableFragment<T extends IIdSettable & Model> extends EditableFragmentBase<T> {

    protected NowListAdapter<T> mAdapter;
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
    protected T getItem(int id) {
        return mAdapter.getItem(id);
    }

    @Override
    protected void addItem(int pos, T item) {
        mAdapter.add(pos, item);
    }

    @Override
    protected void removeItem(int pos) {
        mAdapter.remove(pos);
    }
}
