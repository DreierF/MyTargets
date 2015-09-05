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

import com.bignerdranch.android.recyclerviewchoicemode.ModalMultiSelectorCallback;
import com.bignerdranch.android.recyclerviewchoicemode.MultiSelector;
import com.bignerdranch.android.recyclerviewchoicemode.OnCardClickListener;
import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IdProvider;

public abstract class EditableNowListFragmentBase<T extends IdProvider> extends NowListFragmentBase<T>
        implements OnCardClickListener<T>, LoaderManager.LoaderCallbacks<List<T>> {

    // Action mode handling
    final MultiSelector mSelector = new MultiSelector();
    @PluralsRes
    int itemTypeDelRes;

    public IdProviderDataSource<T> dataSource;

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
            edit.setVisible(getSelectedCount() == 1);
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
        final ArrayList<T> deleted = new ArrayList<>();
        for (int pos : positions) {
            T item = getItem(pos);
            deleted.add(item);
            removeItem(pos);
        }
        String message = getActivity().getResources()
                .getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        Snackbar.make(rootView, message,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    for (T item : deleted) {
                        dataSource.update(item);
                    }
                }).show();
        for (T item : deleted) {
            dataSource.delete(item);
        } //TODO make this more intelligent and support animations
    }

    protected abstract void removeItem(int pos);

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = mSelector.getSelectedPositions().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = getResources().getQuantityString(itemTypeRes, count, count);
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
}
