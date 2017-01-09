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

package de.dreier.mytargets.features.settings.backup.synchronization;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.features.settings.backup.EBackupInterval;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    private static final long ONE_DAY = 86400;  // 1 day (in seconds)

    private static final String TAG = "SyncUtils";

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void createSyncAccount(Context context) {
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, BuildConfig.CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, BuildConfig.CONTENT_AUTHORITY, false);
        }
    }

    public static void setSyncAccountPeriodicSync(EBackupInterval interval) {
        Account account = GenericAccountService.getAccount();
        if (interval == null) {
            ContentResolver
                    .removePeriodicSync(account, BuildConfig.CONTENT_AUTHORITY, new Bundle());
        } else {
            ContentResolver.addPeriodicSync(account, BuildConfig.CONTENT_AUTHORITY,
                    new Bundle(), interval.getDays() * ONE_DAY);
        }
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p>
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     * <p>
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void triggerBackup() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        Log.d(TAG, "triggerBackup: ");
        ContentResolver.requestSync(
                GenericAccountService.getAccount(),
                BuildConfig.CONTENT_AUTHORITY,
                b);
    }
}
