/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.utils;

import android.content.Context;

import de.dreier.mytargets.managers.dao.IdProviderDataSource;
import de.dreier.mytargets.shared.models.IIdSettable;

public class DataLoader<T extends IIdSettable> extends DataLoaderBase<T, IdProviderDataSource<T>> {

    public DataLoader(Context context, IdProviderDataSource<T> dataSource, BackgroundAction<T> a) {
        super(context, dataSource, a);
    }

    @SuppressWarnings("unchecked")
    public void update(T entity) {
        new UpdateTask(this).execute(entity);
    }

    @SuppressWarnings("unchecked")
    public void delete(T entity) {
        new DeleteTask(this).execute(entity);
    }

    private class UpdateTask extends ContentChangingTask<T, Void, Void> {
        UpdateTask(DataLoader<T> loader) {
            super(loader);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(T... params) {
            mDataSource.update(params[0]);
            return null;
        }
    }

    private class DeleteTask extends ContentChangingTask<T, Void, Void> {
        DeleteTask(DataLoader<T> loader) {
            super(loader);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(T... params) {
            mDataSource.delete(params[0]);
            return null;
        }
    }
}