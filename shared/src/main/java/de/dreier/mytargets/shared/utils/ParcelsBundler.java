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

package de.dreier.mytargets.shared.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.evernote.android.state.Bundler;

import org.parceler.Parcels;

public class ParcelsBundler<T> implements Bundler<T> {
    @Override
    public void put(@NonNull String s, @NonNull T example, @NonNull Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(example));
    }

    @Override
    public T get(@NonNull String s, @NonNull Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
