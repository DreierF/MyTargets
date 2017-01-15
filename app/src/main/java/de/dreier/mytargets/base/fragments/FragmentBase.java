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

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.google.firebase.analytics.FirebaseAnalytics;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.Utils;
import icepick.Icepick;

/**
 * Generic fragment class used as base for most fragments.
 * Has Icepick build in to save state on orientation change
 * and animates activity when #finish gets called.
 */
public abstract class FragmentBase extends Fragment implements LoaderManager.LoaderCallbacks<FragmentBase.LoaderUICallback> {

    private static final int LOADER_ID = 0;

    public void logEvent(String event) {
        FirebaseAnalytics.getInstance(getContext()).logEvent(event, null);
    }

    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logEvent(getClass().getSimpleName());
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected void finish() {
        if (Utils.isLollipop()) {
            getActivity().finishAfterTransition();
        } else {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadData();
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
        return () -> {
        };
    }

    @Override
    public void onLoadFinished(Loader<LoaderUICallback> loader, LoaderUICallback callback) {
        callback.applyData();
    }

    @Override
    public void onLoaderReset(Loader<LoaderUICallback> loader) {

    }

    protected void reloadData() {
        if (getLoaderManager().getLoader(LOADER_ID) != null) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
        getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
    }

    protected void reloadData(Bundle args) {
        if (getLoaderManager().getLoader(LOADER_ID) != null) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
        getLoaderManager().restartLoader(LOADER_ID, args, this).forceLoad();
    }

    public interface LoaderUICallback {
        @UiThread
        void applyData();
    }
}
