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

import junit.framework.Assert;

import org.parceler.Parcels;

import de.dreier.mytargets.base.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.SingleSelectorBundler;
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import de.dreier.mytargets.utils.multiselector.SingleSelector;
import icepick.State;

/**
 * Base class for handling single item selection
 * <p>
 * Parent activity must implement {@link OnItemSelectedListener}.
 *
 * @param <T> Model of the item which is managed within the fragment.
 */
public abstract class SelectItemFragmentBase<T extends IIdProvider & Comparable<T>,
        U extends ListAdapterBase<? extends ItemBindingHolder<?>, T>> extends ListFragmentBase<T, U> {

    /**
     * Selector which manages the item selection
     */
    @State(SingleSelectorBundler.class)
    protected SingleSelector selector = new SingleSelector();

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
        selector.setSelectable(true);
    }

    /**
     * Searches the recyclerView's adapter for the given item and sets it as selected.
     * If the position is not visible on the screen it is scrolled into view.
     *
     * @param recyclerView RecyclerView instance
     * @param item         Currently selected item
     */
    protected void selectItem(RecyclerView recyclerView, T item) {
        selector.setSelected(item.getId(), true);
        recyclerView.post(() -> {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int first = manager.findFirstCompletelyVisibleItemPosition();
            int last = manager.findLastCompletelyVisibleItemPosition();
            int position = adapter.getItemPosition(item);
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
    public void onClick(SelectableViewHolder<T> holder, T item) {
        boolean alreadySelected = selector.isSelected(holder.getItemId());
        selector.setSelected(holder, true);
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
        return adapter.getItemById(selector.getSelectedId());
    }
}
