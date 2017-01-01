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

package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;

import de.dreier.mytargets.interfaces.ItemAdapter;

public abstract class ListAdapterBase<S extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<S>
        implements ItemAdapter<T> {
}
