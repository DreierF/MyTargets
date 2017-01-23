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
package de.dreier.mytargets.features.training.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.structure.Model;

import java.util.List;

import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter;
import de.dreier.mytargets.base.fragments.EditableListFragmentBase;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.LongUtils;

/**
 * Shows all rounds of one training day
 */
public abstract class ExpandableListFragment<H extends IIdProvider, C extends IIdSettable & Model>
        extends EditableListFragmentBase<C, ExpandableListAdapter<H, C>> {

    private static final String KEY_EXPANDED = "expanded";

    @Nullable
    private Bundle savedInstanceState;

    protected void setList(List<C> children, boolean opened) {
        if (adapter.getItemCount() == 0) {
            adapter.setList(children, opened);
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EXPANDED)) {
                adapter.setExpandedIds(
                        LongUtils.toList(savedInstanceState.getLongArray(KEY_EXPANDED)));
            } else if (!opened && adapter.getItemCount() > 0) {
                adapter.expandFirst();
            }
            return;
        }
        adapter.setList(children);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putLongArray(KEY_EXPANDED, LongUtils.toArray(adapter.getExpandedIds()));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

}
