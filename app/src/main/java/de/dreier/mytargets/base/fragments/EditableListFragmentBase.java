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

import android.support.annotation.PluralsRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.ListAdapterBase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.utils.MultiSelectorBundler;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import icepick.State;

/**
 * @param <T> Model of the item which is managed within the fragment.
 */
public abstract class EditableListFragmentBase<T extends IIdSettable & IRecursiveModel,
        U extends ListAdapterBase<?, T>> extends ListFragmentBase<T, U> {

    public static final String ITEM_ID = "id";

    protected boolean supportsStatistics = false;
    protected boolean supportsDeletion = true;
    @State(MultiSelectorBundler.class)
    protected MultiSelector selector = new MultiSelector();

    /**
     * Resource used to set title when items are selected.
     */
    @PluralsRes
    protected int itemTypeSelRes;

    /**
     * Resource used to set title when items are deleted.
     */
    @PluralsRes
    protected int itemTypeDelRes;

    /**
     * Action mode manager
     */
    ActionMode actionMode = null;

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(selector.getSelectedIds().size() == 1);
            menu.findItem(R.id.action_statistics).setVisible(supportsStatistics);
            menu.findItem(R.id.action_delete).setVisible(supportsDeletion);
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            selector.setSelectable(true);
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_edit_delete, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    onEdit(getSelectedItems().get(0));
                    mode.finish();
                    return true;
                case R.id.action_statistics:
                    onStatistics(getSelectedItems());
                    mode.finish();
                    return true;
                case R.id.action_delete:
                    remove(getSelectedItems());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        private List<T> getSelectedItems() {
            List<Long> ids = selector.getSelectedIds();
            return Stream.of(ids)
                    .map(id -> adapter.getItemById(id))
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selector.setSelectable(false);
            selector.clearSelections();
            actionMode = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void remove(List<T> deleted) {
        FirebaseAnalytics.getInstance(getContext()).logEvent("delete", null);
        for (T item : deleted) {
            adapter.removeItem(item);
            item.delete();
        }
        adapter.notifyDataSetChanged();
        String message = getResources()
                .getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        Snackbar.make(getView().findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    for (T item : deleted) {
                        item.saveRecursively();
                        adapter.addItem(item);
                    }
                    deleted.clear();
                })
                .show();
    }

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = selector.getSelectedIds().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = getResources().getQuantityString(itemTypeSelRes, count, count);
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    @Override
    public void onClick(SelectableViewHolder<T> holder, T item) {
        if (!selector.tapSelection(holder)) {
            onSelected(item);
        } else {
            updateTitle();
        }
    }

    @Override
    public void onLongClick(SelectableViewHolder<T> holder) {
        if (actionMode == null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.startSupportActionMode(actionModeCallback);
            selector.setSelectable(true);
        }
        selector.setSelected(holder, true);
        updateTitle();
    }

    protected abstract void onEdit(T item);

    /**
     * @param item Item that has been selected
     */
    protected abstract void onSelected(T item);

    /**
     * @param items Items that have been selected
     */
    protected void onStatistics(List<T> items) {
    }
}
