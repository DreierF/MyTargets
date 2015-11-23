/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;

import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IdProvider;

public class DataLoader<T extends IdProvider> extends DataLoaderBase<T, IdProviderDataSource<T>> {

    public DataLoader(Context context, IdProviderDataSource<T> dataSource, BackgroundAction<T> a) {
        super(context, dataSource, a);
    }

    public void update(T entity) {
        new UpdateTask(this).execute(entity);
    }

    public void delete(T entity) {
        new DeleteTask(this).execute(entity);
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
}