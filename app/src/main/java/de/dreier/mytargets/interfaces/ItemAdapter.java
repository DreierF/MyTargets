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

package de.dreier.mytargets.interfaces;

import java.util.List;

public interface ItemAdapter<T> {
    void notifyDataSetChanged();
    void removeItem(T item);
    void addItem(T item);
    T getItem(int position);
    T getItemById(long id);
    int getItemPosition(T item);
    void setList(List<T> data);
}
