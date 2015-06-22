/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import java.util.List;

import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.IdProvider;
import de.dreier.mytargets.views.CardItemDecorator;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class NowListFragment<T extends IdProvider> extends NowListFragmentBase<T> {

    NowListAdapter<T> mAdapter;
    protected OnItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new CardItemDecorator(getActivity()));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) getActivity();
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    void setList(List<T> list, NowListAdapter<T> adapter) {
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
    protected void removeItem(int pos) {
        mAdapter.remove(pos);
    }

    protected final void onSelected(T item) {
        listener.onItemSelected(item.getId(), item.getClass());
    }

    public interface OnItemSelectedListener {
        void onItemSelected(long itemId, Class<? extends IdProvider> aClass);

        void onItemSelected(IdProvider e);
    }
}
