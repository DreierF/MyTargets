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

package de.dreier.mytargets.base.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class ListAdapterBase<S extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<S> {
    public abstract void removeItem(T item);

    public abstract void addItem(T item);

    public abstract T getItem(int position);

    public abstract T getItemById(long id);

    public abstract int getItemPosition(T item);

    public abstract void setList(List<T> data);
}
