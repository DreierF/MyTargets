/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
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

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.SingleSelector;

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
    NowListAdapter<T> mAdapter;

    /**
     * Listener which gets called when item gets selected
     */
    private OnItemSelectedListener listener;
    boolean useDoubleClickSelection;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        if (useDoubleClickSelection) {
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
    void setList(List<T> list, NowListAdapter<T> adapter) {
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
        int oldSelectedPosition = mSelector.getSelectedPosition();
        boolean alreadySelected = oldSelectedPosition == holder.getAdapterPosition();
        mSelector.setSelected(holder, true);
        if (alreadySelected || !useDoubleClickSelection) {
            onSaveItem();
        } else {
            mAdapter.notifyItemChanged(oldSelectedPosition);
            mAdapter.notifyItemChanged(holder.getAdapterPosition());
        }
    }

    /**
     * Returns the selected item to the calling activity
     */
    private void onSaveItem() {
        listener.onItemSelected(Parcels.wrap(onSave()));
    }

    /**
     * Gets the item that has been selected by the user
     *
     * @return The selected item
     */
    T onSave() {
        return mAdapter.getItem(mSelector.getSelectedPosition());
    }
}
