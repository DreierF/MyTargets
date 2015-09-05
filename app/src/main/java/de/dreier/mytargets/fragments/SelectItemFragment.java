/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;
import com.bignerdranch.android.recyclerviewchoicemode.SingleSelector;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.IIdProvider;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class SelectItemFragment<T extends IIdProvider> extends NowListFragmentBase<T> {

    // Action mode handling
    final SingleSelector mSelector = new SingleSelector();
    NowListAdapter<T> mAdapter;
    private OnItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
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

    protected final void onSelected(T item) {
        listener.onItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSaveItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(SelectableViewHolder holder, T mItem) {
        if (mItem == null) {
            return;
        }
        if (mSelector.getSelectedPosition() == holder.getAdapterPosition()) {
            onSaveItem();
        }
        mSelector.setSelected(holder, true);
    }

    private void onSaveItem() {
        Intent data = new Intent();
        data.putExtra(ITEM, (Serializable)onSave());
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    protected T onSave() {
        return mAdapter.getItem(mSelector.getSelectedPosition());
    }
}
