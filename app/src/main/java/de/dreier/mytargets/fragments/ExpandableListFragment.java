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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import de.dreier.mytargets.adapters.ExpandableListAdapter;
import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.Utils;

/**
 * Shows all rounds of one training day
 */
abstract class ExpandableListFragment<H extends IIdProvider, C extends IIdSettable> extends EditableListFragmentBase<C> {

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

    @Override
    protected ItemAdapter<C> getAdapter() {
        return mAdapter;
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
