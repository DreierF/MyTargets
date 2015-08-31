/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.os.AsyncTask;
import android.support.v4.content.Loader;

public abstract class ContentChangingTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    private Loader<?> loader = null;

    ContentChangingTask(Loader<?> loader) {
        this.loader = loader;
    }

    @Override
    protected void onPostExecute(T3 param) {
        loader.onContentChanged();
    }
}
