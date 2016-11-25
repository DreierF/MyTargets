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

package de.dreier.mytargets.shared.utils;

import android.os.Bundle;

import org.parceler.Parcels;

public class ParcelsBundler<T> implements icepick.Bundler<T> {
    @Override
    public void put(String s, T example, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(example));
    }

    @Override
    public T get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
