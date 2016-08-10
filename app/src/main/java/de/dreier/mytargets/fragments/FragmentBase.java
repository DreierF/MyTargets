/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;

/**
 * Generic fragment class used as base for most fragments.
 *
 * @param <T> Model of the item which is managed within the fragment
 */
public abstract class FragmentBase<T extends IIdProvider> extends Fragment
        implements OnCardClickListener<T>, LoaderManager.LoaderCallbacks<FragmentBase.LoaderUICallback> {

    public static final String ITEM_ID = "id";

    /**
     * Resource used to set title when items are selected
     */
    @PluralsRes
    int itemTypeSelRes;

    /**
     * Resource describing FAB action
     */
    @StringRes
    int newStringRes;

    /**
     * Action mode manager
     */
    ActionMode actionMode = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<LoaderUICallback> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<LoaderUICallback>(getContext()) {
            @Override
            public LoaderUICallback loadInBackground() {
                return onLoad(args);
            }
        };
    }

    @WorkerThread
    @NonNull
    protected LoaderUICallback onLoad(Bundle args) {
        return () -> {};
    }

    @Override
    public void onLoadFinished(Loader<LoaderUICallback> loader, LoaderUICallback callback) {
        callback.applyData();
    }

    @Override
    public void onLoaderReset(Loader<LoaderUICallback> loader) {

    }

    protected void reloadData() {
        getLoaderManager().restartLoader(0, null, this);
    }

    protected void reloadData(Bundle args) {
        getLoaderManager().restartLoader(0, args, this);
    }

    /**
     * Used for communicating item selection
     */
    public interface OnItemSelectedListener {
        /**
         * Called when a item has been selected.
         *
         * @param item Item that has been selected
         */
        void onItemSelected(Parcelable item);
    }

    public interface LoaderUICallback {
        @UiThread
        void applyData();
    }
}
