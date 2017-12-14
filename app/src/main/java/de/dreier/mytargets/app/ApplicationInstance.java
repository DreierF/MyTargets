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
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import java.io.File;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.WindDirection;
import de.dreier.mytargets.shared.models.WindSpeed;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.models.db.Signature;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.utils.ImageList;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.backup.MyBackupAgent;
import im.delight.android.languages.Language;
import timber.log.Timber;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(Environment.class),
        @ParcelClass(End.class),
        @ParcelClass(EndRenderer.class),
        @ParcelClass(ImageList.class),
        @ParcelClass(Round.class),
        @ParcelClass(RoundTemplate.class),
        @ParcelClass(Score.class),
        @ParcelClass(Shot.class),
        @ParcelClass(SightMark.class),
        @ParcelClass(Signature.class),
        @ParcelClass(StandardRound.class),
        @ParcelClass(TrainingInfo.class),
        @ParcelClass(Target.class),
        @ParcelClass(TimerSettings.class),
        @ParcelClass(Training.class),
        @ParcelClass(WindDirection.class),
        @ParcelClass(WindSpeed.class)
})
public class ApplicationInstance extends SharedApplicationInstance {

    public static MobileWearableClient wearableClient;

    public static SharedPreferences getLastSharedPreferences() {
        return Companion.getContext().getSharedPreferences(MyBackupAgent.PREFS, 0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(BuildConfig.DEBUG) {
            MultiDex.install(this);
        }
    }

    @Override
    public void onCreate() {
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE);
        if (BuildConfig.DEBUG) {
            enableDebugLogging();
        } else {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE);
    }

    @Override
    public void onTerminate() {
        FlowManager.destroy();
        wearableClient.disconnect();
        super.onTerminate();
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, @NonNull String message, @Nullable Throwable t) {
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
