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

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.design.widget.Snackbar;
import android.view.View;


import de.dreier.mytargets.shared.streamwrapper.Stream;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.utils.MultiSelectorBundler;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.OnItemLongClickListener;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import com.evernote.android.state.State;

/**
 * @param <T> Model of the item which is managed within the fragment.
 */
public abstract class EditableListFragmentBase<T extends IIdSettable & IRecursiveModel,
        U extends ListAdapterBase<?, T>> extends ListFragmentBase<T, U>
        implements OnItemLongClickListener<T> {

    public static final String ITEM_ID = "id";

    @State(MultiSelectorBundler.class)
    protected MultiSelector selector = new MultiSelector();

    /**
     * Resource used to set title when items are deleted.
     */
    @PluralsRes
    protected int itemTypeDelRes;

    public ItemActionModeCallback actionModeCallback;

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    public void onDelete(@NonNull List<Long> deletedIds) {
        FirebaseAnalytics.getInstance(getContext()).logEvent("delete", null);
        List<T> deleted = deleteItems(deletedIds);
        String message = getResources()
                .getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        View coordinatorLayout = getView().findViewById(R.id.coordinatorLayout);
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> undoDeletion(deleted))
                .show();
    }

    @NonNull
    private List<T> deleteItems(@NonNull List<Long> deletedIds) {
        List<T> deleted = Stream.of(deletedIds)
                .map(id -> adapter.getItemById(id))
                .filter(item -> item != null)
                .toList();
        for (T item : deleted) {
            adapter.removeItem(item);
            item.delete();
        }
        adapter.notifyDataSetChanged();
        reloadData();
        return deleted;
    }

    private void undoDeletion(@NonNull List<T> deleted) {
        for (T item : deleted) {
            item.saveRecursively();
            adapter.addItem(item);
        }
        reloadData();
        deleted.clear();
    }

    @Override
    public void onClick(SelectableViewHolder<T> holder, T item) {
        if (!actionModeCallback.click(holder)) {
            onSelected(item);
        }
    }

    @Override
    public final void onLongClick(SelectableViewHolder<T> holder) {
        actionModeCallback.longClick(holder);
    }

    protected abstract void onSelected(T item);

}
