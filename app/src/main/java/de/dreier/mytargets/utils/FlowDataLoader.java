/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;

import com.raizlabs.android.dbflow.structure.BaseModel;

public class FlowDataLoader<T extends BaseModel> extends FlowDataLoaderBase<T> {

    public FlowDataLoader(Context context, BackgroundAction<T> a) {
        super(context, a);
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
        UpdateTask(FlowDataLoader<T> loader) {
            super(loader);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(T... params) {
            params[0].save();
            return null;
        }
    }

    private class DeleteTask extends ContentChangingTask<T, Void, Void> {
        DeleteTask(FlowDataLoader<T> loader) {
            super(loader);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(T... params) {
            params[0].delete();
            return null;
        }
    }
}