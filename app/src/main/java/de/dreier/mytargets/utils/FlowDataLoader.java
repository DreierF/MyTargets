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
}