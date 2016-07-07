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

import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.Utils;

/**
 * Shows all rounds of one training day
 */
abstract class ExpandableFragment<H extends IIdProvider, C extends IIdSettable> extends EditableFragmentBase<C> {

    private static final String KEY_EXPANDED = "expanded";
    ExpandableNowListAdapter<H, C> mAdapter;
    @Nullable
    private Bundle savedInstanceState;

    void setList(IdProviderDataSource<C> dataSource, List<H> headers, List<C> children, PartitionDelegate<C> parentDelegate, boolean opened) {
        this.dataSource = dataSource;
        if (mAdapter.getItemCount() == 0) {
            mAdapter.setList(headers, children, parentDelegate, opened);
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EXPANDED)) {
                mAdapter.setExpandedIds(
                        Utils.toList(savedInstanceState.getLongArray(KEY_EXPANDED)));
            } else if (!opened && mAdapter.getItemCount() > 0) {
                mAdapter.expandOrCollapse(0);
            }
            return;
        }
        mAdapter.setList(headers, children, parentDelegate);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    protected C getItem(int id) {
        return (C) mAdapter.getItem(id);
    }

    @Override
    protected void removeItem(int pos) {
        mAdapter.remove(pos);
    }

    @Override
    protected void addItem(int pos, C item) {
        mAdapter.add(pos, item);
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
