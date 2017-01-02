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
package de.dreier.mytargets.fragments;

import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.SelectorBundler;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import icepick.State;

/**
 * @param <T> Model of the item which is managed within the fragment.
 */
public abstract class EditableListFragmentBase<T extends IIdSettable & Model> extends ListFragmentBase<T> {

    protected static final String ITEM_ID = "id";

    protected boolean supportsStatistics = false;
    protected boolean supportsDeletion = true;
    @State(SelectorBundler.class)
    MultiSelector mSelector = new MultiSelector();

    /**
     * Resource describing FAB action
     */
    @StringRes
    int newStringRes;

    /**
     * Resource used to set title when items are selected.
     */
    @PluralsRes
    int itemTypeSelRes;

    /**
     * Resource used to set title when items are deleted.
     */
    @PluralsRes
    int itemTypeDelRes;

    /**
     * Action mode manager
     */
    ActionMode actionMode = null;

    private final ActionMode.Callback mDeleteMode = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(mSelector.getSelectedIds().size() == 1);
            menu.findItem(R.id.action_statistics).setVisible(supportsStatistics);
            menu.findItem(R.id.action_delete).setVisible(supportsDeletion);
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mSelector.setSelectable(true);
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
            List<Long> ids = mSelector.getSelectedIds();
            return Stream.of(ids)
                    .map(id -> getAdapter().getItemById(id))
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSelector.setSelectable(false);
            mSelector.clearSelections();
            actionMode = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void remove(List<T> deleted) {
        for (T item : deleted) {
            getAdapter().removeItem(item);
        }
        getAdapter().notifyDataSetChanged();
        String message = getResources()
                .getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        Snackbar.make(getView().findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    for (T item : deleted) {
                        getAdapter().addItem(item);
                    }
                    deleted.clear();
                })
                .addCallback(
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (T item : deleted) {
                                    item.delete();
                                }
                                if (isAdded()) {
                                    reloadData();
                                }
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }
                        }).show();
    }

    protected abstract ItemAdapter<T> getAdapter();

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = mSelector.getSelectedIds().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = getResources().getQuantityString(itemTypeSelRes, count, count);
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    public void onClick(SelectableViewHolder holder, T mItem) {
        if (mItem == null) {
            return;
        }
        if (!mSelector.tapSelection(holder)) {
            onSelected(mItem);
        } else {
            updateTitle();
        }
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        if (actionMode == null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.startSupportActionMode(mDeleteMode);
            mSelector.setSelectable(true);
        }
        mSelector.setSelected(holder, true);
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
