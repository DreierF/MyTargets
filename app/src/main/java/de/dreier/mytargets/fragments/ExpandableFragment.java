/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IdProvider;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class ExpandableFragment<H extends IdProvider, C extends IdProvider>
        extends EditableFragmentBase<C> {

    ExpandableNowListAdapter<H, C> mAdapter;
    private GridLayoutManager manager;

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

    void setList(IdProviderDataSource<C> dataSource, List<H> list, List<C> children, boolean opened, ExpandableNowListAdapter<H, C> adapter) {
        this.dataSource = dataSource;
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = adapter;
            mAdapter.setList(list, children, opened);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(list, children, opened);
            mAdapter.notifyDataSetChanged();
        }
        updateFabButton(list);
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
}
