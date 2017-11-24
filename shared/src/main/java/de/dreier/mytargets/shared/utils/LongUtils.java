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

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LongUtils {
    @NonNull
    public static long[] toArray(@NonNull List<Long> values) {
        long[] result = new long[values.size()];
        int i = 0;
        for (Long l : values) {
            result[i++] = l;
        }
        return result;
    }

    @NonNull
    public static List<Long> toList(@NonNull long[] array) {
        List<Long> list = new ArrayList<>();
        for (long value : array) {
            list.add(value);
        }
        return list;
    }
}
