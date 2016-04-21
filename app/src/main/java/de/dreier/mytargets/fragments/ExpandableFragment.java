/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.Utils;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class ExpandableFragment<H extends IIdProvider, C extends IIdSettable>
        extends EditableFragmentBase<C> {

    ExpandableNowListAdapter<H, C> mAdapter;
    private GridLayoutManager manager;
    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        manager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(manager);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter != null) {
                    return mAdapter.isHeader(position) ? manager.getSpanCount() : 1;
                }
                return 1;
            }
        });

        return rootView;
    }

    void setList(IdProviderDataSource<C> dataSource, List<H> headers, List<C> children, PartitionDelegate<C> parentDelegate, boolean opened, ExpandableNowListAdapter<H, C> adapter) {
        this.dataSource = dataSource;
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = adapter;
            mAdapter.setList(headers, children, parentDelegate, opened);
            if (savedInstanceState != null) {
                mAdapter.setExpandedIds(Utils.toList(savedInstanceState.getLongArray("expanded")));
            } else if (!opened && mAdapter.getItemCount() > 0) {
                mAdapter.expandOrCollapse(0);
            }
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(headers, children, parentDelegate);
            mAdapter.notifyDataSetChanged();
        }

        updateFabButton(headers);
        manager.setSpanCount(adapter.getMaxSpan());
    }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLongArray("expanded", Utils.toArray(mAdapter.getExpandedIds()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }
}
