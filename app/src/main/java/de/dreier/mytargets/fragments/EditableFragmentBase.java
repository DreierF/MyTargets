/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.OnCardClickListener;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

abstract class EditableFragmentBase<T extends IIdSettable> extends FragmentBase<T>
        implements OnCardClickListener<T>, LoaderManager.LoaderCallbacks<List<T>> {

    protected boolean supportsStatistics = false;
    final MultiSelector mSelector = new MultiSelector();
    @PluralsRes
    int itemTypeDelRes;
    IdProviderDataSource<T> dataSource;
    private final ActionMode.Callback mDeleteMode = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(mSelector.getSelectedIds().size() == 1);
            MenuItem stats = menu.findItem(R.id.action_statistics);
            stats.setVisible(supportsStatistics);
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mSelector.clearSelections();
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
                    onStatistics(mSelector.getSelectedIds());
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

        protected List<T> getSelectedItems() {
            List<Long> ids = mSelector.getSelectedIds();
            return Stream.of(ids)
                    .map(id -> getItem(id))
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onLoaderReset(Loader<List<T>> loader) {
        // Called when the loader is restarted, but we
        // don't want to remove all elements from the
        // screen on resume just in case something changed
    }

    private void remove(List<T> deleted) {
        for (T item : deleted) {
            removeItem(item);
        }
        String message = getResources().getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    for (T item : deleted) {
                        addItem(item);
                    }
                    deleted.clear();
                })
                .setCallback(
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (T item : deleted) {
                                    dataSource.delete(item);
                                }
                                if (isAdded()) {
                                    getLoaderManager()
                                            .restartLoader(0, null, EditableFragmentBase.this);
                                }
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }
                        }).show();
    }

    protected abstract void addItem(T item);

    protected abstract void removeItem(T item);

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
     * @param itemIds Items that have been selected
     */
    protected void onStatistics(List<Long> itemIds) {}

    /**
     * Gets the item by a given id
     *
     * @param id Id to get the item for
     * @return Item with the given id
     */
    protected abstract T getItem(long id);
}
