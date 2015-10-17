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

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.SelectableViewHolder;

public abstract class EditableFragmentBase<T extends IdProvider> extends FragmentBase<T>
        implements OnCardClickListener<T>, LoaderManager.LoaderCallbacks<List<T>> {


    @PluralsRes
    protected int itemTypeDelRes;

    protected IdProviderDataSource<T> dataSource;
    final protected MultiSelector mSelector = new MultiSelector();

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

    }

    private final ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(
            mSelector) {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(mSelector.getSelectedPositions().size() == 1);
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_edit_delete, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    int id = mSelector.getSelectedPositions().get(0);
                    onEdit(getItem(id));
                    mode.finish();
                    return true;
                case R.id.action_delete:
                    List<Integer> positions = mSelector.getSelectedPositions();
                    remove(positions);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            actionMode = null;
        }
    };

    private void remove(List<Integer> positions) {
        Collections.sort(positions);
        Collections.reverse(positions);
        final ArrayList<Pair<Integer, T>> deleted = new ArrayList<>();
        for (int pos : positions) {
            deleted.add(new Pair<>(pos, getItem(pos)));
            removeItem(pos);
        }
        Collections.reverse(deleted);
        String message = getResources().getQuantityString(itemTypeDelRes, deleted.size(),
                deleted.size());
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    for (Pair<Integer, T> item : deleted) {
                        addItem(item.getFirst(), item.getSecond());
                    }
                    deleted.clear();
                })
                .setCallback(
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (Pair<Integer, T> item : deleted) {
                                    dataSource.delete(item.getSecond());
                                }
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }
                        })
                .show();
    }

    protected abstract void addItem(int pos, T item);

    protected abstract void removeItem(int pos);

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = mSelector.getSelectedPositions().size();
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
     * @param item
     */
    protected abstract void onSelected(T item);

    /**
     * Gets the item by a given id
     *
     * @param id Id to get the item for
     * @return Item with the given id
     */
    protected abstract T getItem(int id);
}
