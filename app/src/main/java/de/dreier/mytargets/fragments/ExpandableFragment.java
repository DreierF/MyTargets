/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import de.dreier.mytargets.adapters.ExpandableListAdapter;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.Utils;

/**
 * Shows all rounds of one training day
 */
abstract class ExpandableFragment<H extends IIdProvider, C extends IIdSettable> extends EditableFragmentBase<C> {

    private static final String KEY_EXPANDED = "expanded";
    ExpandableListAdapter<H, C> mAdapter;
    @Nullable
    private Bundle savedInstanceState;

    void setList(IdProviderDataSource<C> dataSource, List<C> children, boolean opened) {
        this.dataSource = dataSource;
        if (mAdapter.getItemCount() == 0) {
            mAdapter.setList(children, opened);
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EXPANDED)) {
                mAdapter.setExpandedIds(
                        Utils.toList(savedInstanceState.getLongArray(KEY_EXPANDED)));
            } else if (!opened && mAdapter.getItemCount() > 0) {
                mAdapter.expandFirst();
            }
            return;
        }
        mAdapter.setList(children);
    }

    @NonNull
    @Override
    protected C getItem(long id) {
        return mAdapter.getItemById(id);
    }

    @Override
    protected void removeItem(C item) {
        mAdapter.remove(item);
    }

    @Override
    protected void addItem(C item) {
        mAdapter.add(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putLongArray(KEY_EXPANDED, Utils.toArray(mAdapter.getExpandedIds()));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

}
