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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public class ItemActionModeCallback implements ActionMode.Callback {

    private ActionMode actionMode = null;
    private final MultiSelector selector;
    private final ListFragmentBase fragment;

    /**
     * Callbacks for edit, delete and statistics
     * Null values indicate that the operation is not supported for the presented item type.
     */
    private EditCallback editCallback;
    private DeleteCallback deleteCallback;
    private StatisticsCallback statisticsCallback;

    /**
     * Resource used to set title when items are selected.
     */
    @PluralsRes
    private final int itemTypeSelRes;

    public ItemActionModeCallback(ListFragmentBase fragment, MultiSelector selector, int itemTypeSelRes) {
        this.fragment = fragment;
        this.selector = selector;
        this.itemTypeSelRes = itemTypeSelRes;
        this.statisticsCallback = ((fragment instanceof StatisticsCallback) ?
                (StatisticsCallback) fragment : null);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem edit = menu.findItem(R.id.action_edit);
        edit.setVisible(selector.getSelectedIds().size() == 1);
        menu.findItem(R.id.action_statistics).setVisible(statisticsCallback != null);
        menu.findItem(R.id.action_delete).setVisible(deleteCallback != null);
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
        List<Long> ids = selector.getSelectedIds();
        switch (item.getItemId()) {
            case R.id.action_edit:
                editCallback.onEdit(ids.get(0));
                mode.finish();
                return true;
            case R.id.action_statistics:
                statisticsCallback.onStatistics(ids);
                mode.finish();
                return true;
            case R.id.action_delete:
                deleteCallback.onDelete(ids);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selector.setSelectable(false);
        selector.clearSelections();
        actionMode = null;
    }

    public void longClick(SelectableViewHolder holder) {
        if (actionMode == null) {
            AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
            activity.startSupportActionMode(this);
            selector.setSelectable(true);
        }
        selector.setSelected(holder, true);
        updateTitle();
    }

    /**
     * Returns true if the click has been handled.
     */
    public boolean click(SelectableViewHolder holder) {
        if (selector.tapSelection(holder)) {
            updateTitle();
            return true;
        }
        return false;
    }

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = selector.getSelectedIds().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = fragment.getResources()
                    .getQuantityString(itemTypeSelRes, count, count);
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    public void setEditCallback(EditCallback editCallback) {
        this.editCallback = editCallback;
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public void setStatisticsCallback(StatisticsCallback statisticsCallback) {
        this.statisticsCallback = statisticsCallback;
    }

    public interface EditCallback {
        void onEdit(Long id);
    }

    public interface DeleteCallback {
        void onDelete(List<Long> ids);
    }

    public interface StatisticsCallback {
        void onStatistics(List<Long> ids);
    }
}
