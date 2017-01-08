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

package de.dreier.mytargets.base.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import junit.framework.Assert;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import de.dreier.mytargets.utils.multiselector.SingleSelector;

/**
 * Base class for handling single item selection
 * <p>
 * Parent activity must implement {@link OnItemSelectedListener}.
 *
 * @param <T> Model of the item which is managed within the fragment.
 */
public abstract class SelectItemFragmentBase<T extends IIdProvider & Comparable<T>> extends ListFragmentBase<T> {

    /**
     * Selector which manages the item selection
     */
    protected final SingleSelector mSelector = new SingleSelector();

    /**
     * Adapter for the fragment's RecyclerView
     */
    protected ListAdapterBase<? extends ItemBindingHolder<?>, T> adapter;

    /**
     * Set to true when items are expanded when they are clicked and
     * selected only after hitting them the second time.
     */
    protected boolean useDoubleClickSelection = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSelector.setSelectable(true);
    }

    /**
     * Searches the recyclerView's adapter for the given item and sets it as selected.
     * If the position is not visible on the screen it is scrolled into view.
     *
     * @param recyclerView RecyclerView instance
     * @param item         Currently selected item
     */
    protected void selectItem(RecyclerView recyclerView, T item) {
        int position = adapter.getItemPosition(item);
        mSelector.setSelected(position, item.getId(), true);
        recyclerView.post(() -> {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int first = manager.findFirstCompletelyVisibleItemPosition();
            int last = manager.findLastCompletelyVisibleItemPosition();
            if (first > position || last < position) {
                recyclerView.scrollToPosition(position);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Assert.assertNotNull(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveItem();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(SelectableViewHolder<T> holder, T mItem) {
        if (mItem == null) {
            return;
        }
        int oldSelectedPosition = mSelector.getSelectedPosition();
        boolean alreadySelected = oldSelectedPosition == holder.getAdapterPosition();
        mSelector.setSelected(holder, true);
        if (alreadySelected || !useDoubleClickSelection) {
            saveItem();
            finish();
        }
    }

    /**
     * Returns the selected item to the calling activity. The item is retrieved by calling onSave().
     */
    protected void saveItem() {
        listener.onItemSelected(Parcels.wrap(onSave()));
    }

    /**
     * Gets the item that has been selected by the user
     *
     * @return The selected item
     */
    protected T onSave() {
        return adapter.getItem(mSelector.getSelectedPosition());
    }
}
