/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IdProvider;

public class DataLoader<T extends IdProvider> extends AsyncTaskLoader<List<T>> {
    private IdProviderDataSource<T> mDataSource;
    private BackgroundAction backgroundAction;

    public DataLoader(Context context, IdProviderDataSource<T> dataSource, BackgroundAction a) {
        super(context);
        mDataSource = dataSource;
        backgroundAction = a;
    }

    public void update(T entity) {
        new UpdateTask(this).execute(entity);
    }

    public void delete(T entity) {
        new DeleteTask(this).execute(entity);
    }

    protected List<T> mLastDataList = null;

    /**
     * Runs on a worker thread, loading in our data. Delegates the real work to
     * concrete subclass' buildList() method.
     */
    @Override
    public List<T> loadInBackground() {
        return backgroundAction.run();
    }

    /**
     * Runs on the UI thread, routing the results from the background thread to
     * whatever is using the dataList.
     */
    @Override
    public void deliverResult(List<T> dataList) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            emptyDataList(dataList);
            return;
        }
        List<T> oldDataList = mLastDataList;
        mLastDataList = dataList;
        if (isStarted()) {
            super.deliverResult(dataList);
        }
        if (oldDataList != null && oldDataList != dataList && oldDataList.size() > 0) {
            emptyDataList(oldDataList);
        }
    }

    /**
     * Starts an asynchronous load of the list data. When the result is ready
     * the callbacks will be called on the UI thread. If a previous load has
     * been completed and is still valid the result may be passed to the
     * callbacks immediately.
     * <p>
     * Must be called from the UI thread.
     */
    @Override
    protected void onStartLoading() {
        if (mLastDataList != null) {
            deliverResult(mLastDataList);
        }
        if (takeContentChanged() || mLastDataList == null
                || mLastDataList.size() == 0) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to stopLoading().
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    public void onCanceled(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            emptyDataList(dataList);
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to reset(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        if (mLastDataList != null && mLastDataList.size() > 0) {
            emptyDataList(mLastDataList);
        }
        mLastDataList = null;
    }

    protected void emptyDataList(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                dataList.remove(i);
            }
        }
    }

    private class UpdateTask extends ContentChangingTask<T, Void, Void> {
        UpdateTask(DataLoader<T> loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(T... params) {
            mDataSource.update(params[0]);
            return null;
        }
    }

    private class DeleteTask extends ContentChangingTask<T, Void, Void> {
        DeleteTask(DataLoader<T> loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(T... params) {
            mDataSource.delete(params[0]);
            return null;
        }
    }

    public interface BackgroundAction<T> {
        List<T> run();
    }
}