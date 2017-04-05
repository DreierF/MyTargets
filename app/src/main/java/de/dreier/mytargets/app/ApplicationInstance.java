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

package de.dreier.mytargets.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import java.io.File;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.analysis.aggregation.average.Average;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.WindDirection;
import de.dreier.mytargets.shared.models.WindSpeed;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.ArrowImage;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.backup.MyBackupAgent;
import timber.log.Timber;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(Average.class),
        @ParcelClass(Arrow.class),
        @ParcelClass(ArrowImage.class),
        @ParcelClass(Bow.class),
        @ParcelClass(BowImage.class),
        @ParcelClass(Dimension.class),
        @ParcelClass(Environment.class),
        @ParcelClass(End.class),
        @ParcelClass(EndImage.class),
        @ParcelClass(EndRenderer.class),
        @ParcelClass(Round.class),
        @ParcelClass(RoundTemplate.class),
        @ParcelClass(Shot.class),
        @ParcelClass(SightMark.class),
        @ParcelClass(StandardRound.class),
        @ParcelClass(NotificationInfo.class),
        @ParcelClass(Target.class),
        @ParcelClass(Training.class),
        @ParcelClass(Thumbnail.class),
        @ParcelClass(WindDirection.class),
        @ParcelClass(WindSpeed.class)
})
public class ApplicationInstance extends SharedApplicationInstance {

    public static MobileWearableClient wearableClient;

    public static SharedPreferences getLastSharedPreferences() {
        return context.getSharedPreferences(MyBackupAgent.PREFS, 0);
    }

    @Override
    public void onCreate() {
        if (!BuildConfig.DEBUG) {
            Timber.plant(new CrashReportingTree());
        }
        super.onCreate();
        handleDatabaseImport();
        initFlowManager(this);
        wearableClient = new MobileWearableClient(this);
    }

    private void handleDatabaseImport() {
        final File newDatabasePath = getDatabasePath(AppDatabase.DATABASE_FILE_NAME);
        final File oldDatabasePath = getDatabasePath(AppDatabase.DATABASE_IMPORT_FILE_NAME);
        if (oldDatabasePath.exists()) {
            if (newDatabasePath.exists()) {
                newDatabasePath.delete();
            }
            oldDatabasePath.renameTo(newDatabasePath);
        }
    }

    public static void initFlowManager(Context context) {
        FlowManager.init(new FlowConfig.Builder(context).build());
    }

    @Override
    public void onTerminate() {
        FlowManager.destroy();
        wearableClient.disconnect();
        super.onTerminate();
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FirebaseCrash.log(message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FirebaseCrash.report(t);
                } else if (priority == Log.WARN) {
                    FirebaseCrash.report(t);
                }
            }
        }
    }

}
