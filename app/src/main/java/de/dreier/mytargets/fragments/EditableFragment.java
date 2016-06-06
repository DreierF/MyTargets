/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.DividerItemDecoration;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class EditableFragment<T extends IIdSettable> extends EditableFragmentBase<T> {

    private NowListAdapter<T> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        return rootView;
    }

    void setList(IdProviderDataSource<T> dataSource, List<T> list, NowListAdapter<T> adapter) {
        this.dataSource = dataSource;
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = adapter;
            mAdapter.setList(list);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }
        updateFabButton(list);
    }

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
