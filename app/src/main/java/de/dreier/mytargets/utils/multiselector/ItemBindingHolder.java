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

package de.dreier.mytargets.utils.multiselector;

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;

public abstract class ItemBindingHolder<T> extends RebindReportingHolder implements SelectableHolder, View.OnClickListener, View.OnLongClickListener {
    protected T item;

    ItemBindingHolder(View itemView) {
        super(itemView);
    }

    public T getItem() {
        return item;
    }

    void setItem(T mItem) {
        this.item = mItem;
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    public void bindItem(T t) {
        setItem(t);
        bindItem();
    }

    public abstract void bindItem();
}
