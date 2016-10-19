package de.dreier.mytargets.shared.utils;

import java.util.ArrayList;
import java.util.List;

public class LongUtils {
    public static long[] toArray(List<Long> values) {
        long[] result = new long[values.size()];
        int i = 0;
        for (Long l : values) {
            result[i++] = l;
        }
        return result;
    }

    public static List<Long> toList(long[] array) {
        List<Long> list = new ArrayList<>();
        for (long value : array) {
            list.add(value);
        }
        return list;
    }
}
