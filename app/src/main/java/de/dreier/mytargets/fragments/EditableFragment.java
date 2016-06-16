/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import org.parceler.Parcels;

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
    private OnItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        return rootView;
    }

    void setList(IdProviderDataSource<T> dataSource, List<T> list, NowListAdapter<T> adapter) {
        this.dataSource = dataSource;
        if (recyclerView.getAdapter() == null) {
            mAdapter = adapter;
            mAdapter.setList(list);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }
        updateFabButton(list);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        if (getParentFragment() instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) getParentFragment();
        }
        Assert.assertNotNull(listener);
    }

    protected final void onSelected(T item) {
        listener.onItemSelected(Parcels.wrap(item));
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
