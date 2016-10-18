package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.Utils;
import icepick.Icepick;

public abstract class FragmentBase extends Fragment implements LoaderManager.LoaderCallbacks<FragmentBase.LoaderUICallback> {


        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    public interface LoaderUICallback {
        @UiThread
        void applyData();
    }
}
