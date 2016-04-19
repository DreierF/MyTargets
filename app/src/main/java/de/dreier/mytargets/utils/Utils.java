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

import java.util.Calendar;
import java.util.Date;

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

    public static long getMonthId(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(date.getYear() + 1900, date.getMonth(), 1);
        return c.getTimeInMillis();
    }
}
