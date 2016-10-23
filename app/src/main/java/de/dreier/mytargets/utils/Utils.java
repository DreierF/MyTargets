/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.activities.MainActivity;

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

    public static void doRestart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Create a pending intent so the application is restarted after System.exit(0) was called.
        // We use an AlarmManager to call this intent in 100ms
        int mPendingIntentId = 223344;
        PendingIntent mPendingIntent = PendingIntent
                .getActivity(context, mPendingIntentId, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

        // Kill the application
        Process.killProcess(Process.myPid());
    }

    public static boolean isLollipop() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
