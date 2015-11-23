package de.dreier.mytargets.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import de.dreier.mytargets.managers.dao.DataSourceBase;

public class DataLoaderBase<T, DS extends DataSourceBase> extends AsyncTaskLoader<List<T>> {
    protected DS mDataSource;
    protected BackgroundAction<T> backgroundAction;
    protected List<T> mLastDataList = null;

    public DataLoaderBase(Context context, DS dataSource, BackgroundAction<T> a) {
        super(context);
        backgroundAction = a;
        mDataSource = dataSource;
    }

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

    public interface BackgroundAction<T> {
        List<T> run();
    }
}
