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
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.SingleSelector;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

/**
 * Base class for handling single item selection
 * <p>
 * Parent activity must implement {@link de.dreier.mytargets.fragments.FragmentBase.OnItemSelectedListener}.
 */
public abstract class SelectItemFragment<T extends IIdProvider> extends FragmentBase<T> {

    /**
     * Selector which manages the item selection
     */
    final SingleSelector mSelector = new SingleSelector();

    /**
     * Adapter for the fragment's RecyclerView
     */
    protected NowListAdapter<T> mAdapter;

    /**
     * Listener which gets called when item gets selected
     */
    private OnItemSelectedListener listener;
    private boolean useDoubleClickSelection;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        useDoubleClickSelection = toolbar != null;
        if(useDoubleClickSelection) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            assert supportActionBar != null;
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            setHasOptionsMenu(true);
        }

        mSelector.setSelectable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    /**
     * Sets the given list to the fragment's {@link android.support.v7.widget.RecyclerView}.
     * If there is already an adapter attached the adapters content is updated, otherwise the
     * given adapter is initialized with the list and set to the
     * {@link android.support.v7.widget.RecyclerView}.
     *
     * @param list    Content to show
     * @param adapter New instance of an adapter which is able to show the given list
     */
    protected void setList(List<T> list, NowListAdapter<T> adapter) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSaveItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(SelectableViewHolder holder, T mItem) {
        if (mItem == null) {
            return;
        }
        boolean alreadySelected = mSelector.getSelectedPosition() == holder.getAdapterPosition();
        mSelector.setSelected(holder, true);
        if (alreadySelected || !useDoubleClickSelection) {
            onSaveItem();
        }
    }

    /**
     * Returns the selected item to the calling activity
     */
    protected void onSaveItem() {
        Intent data = new Intent();
        data.putExtra(ITEM, (Serializable) onSave());
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /**
     * Gets the item that has been selected by the user
     *
     * @return The selected item
     */
    protected T onSave() {
        return mAdapter.getItem(mSelector.getSelectedPosition());
    }
}
