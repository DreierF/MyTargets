/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static PackageInfo getAppVersionInfo(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return pInfo;
    }

    public static long getMonthId(LocalDate date) {
        return new LocalDate(date).withDayOfMonth(1).toDate().getTime();
    }

    public static long[] toArray(List<Long> values) {
        long[] result = new long[values.size()];
        int i = 0;
        for (Long l : values)
            result[i++] = l;
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
