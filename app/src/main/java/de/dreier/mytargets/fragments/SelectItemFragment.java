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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import junit.framework.Assert;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import de.dreier.mytargets.utils.multiselector.SingleSelector;

/**
 * Base class for handling single item selection
 * <p>
 * Parent activity must implement {@link ListFragmentBase.OnItemSelectedListener}.
 */
public abstract class SelectItemFragment<T extends IIdProvider & Comparable<T>> extends ListFragmentBase<T> {

    /**
     * Selector which manages the item selection
     */
    final SingleSelector mSelector = new SingleSelector();

    /**
     * Adapter for the fragment's RecyclerView
     */
    ListAdapterBase<T> mAdapter;

    /**
     * Listener which gets called when item gets selected
     */
    private OnItemSelectedListener listener;
    boolean usesDoubleClickSelection;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        if (alreadySelected || !usesDoubleClickSelection) {
            onSaveItem();
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
