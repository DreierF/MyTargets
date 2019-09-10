/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDex
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crashlytics.android.Crashlytics
import com.evernote.android.state.StateSaver
import de.dreier.mytargets.BuildConfig
import de.dreier.mytargets.base.db.AppDatabase
import de.dreier.mytargets.base.db.migrations.*
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.utils.MobileWearableClient
import de.dreier.mytargets.utils.backup.MyBackupAgent
import im.delight.android.languages.Language
import timber.log.Timber

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
class ApplicationInstance : SharedApplicationInstance() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (BuildConfig.DEBUG) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        if (BuildConfig.DEBUG) {
            enableDebugLogging()
        } else {
            Timber.plant(CrashReportingTree())
        }
        super.onCreate()
        handleDatabaseImport()
        initRoomDb(this)
        wearableClient = MobileWearableClient(this)
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
    }

    private fun handleDatabaseImport() {
        val newDatabasePath = getDatabasePath(AppDatabase.DATABASE_FILE_NAME)
        val oldDatabasePath = getDatabasePath(AppDatabase.DATABASE_IMPORT_FILE_NAME)
        if (oldDatabasePath.exists()) {
            if (newDatabasePath.exists()) {
                newDatabasePath.delete()
            }
            oldDatabasePath.renameTo(newDatabasePath)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
    }

    override fun onTerminate() {
        db.close()
        wearableClient.disconnect()
        super.onTerminate()
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            Crashlytics.log(message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t)
                } else if (priority == Log.WARN) {
                    Crashlytics.logException(t)
                }
            }
        }
    }

    companion object {

        lateinit var wearableClient: MobileWearableClient
        lateinit var db: AppDatabase

        val lastSharedPreferences: SharedPreferences
            get() = SharedApplicationInstance.context.getSharedPreferences(MyBackupAgent.PREFS, 0)

        fun initRoomDb(context: Context) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, AppDatabase.DATABASE_FILE_NAME
            )
                .allowMainThreadQueries()
                .addCallback(RoomCreationCallback)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .addMigrations(
                    Migration2, Migration3, Migration4,
                    Migration5, Migration6, Migration7,
                    Migration8, Migration9, Migration10,
                    Migration11, Migration12, Migration13,
                    Migration14, Migration15, Migration16,
                    Migration17, Migration18, Migration19,
                    Migration20, Migration21, Migration22,
                    Migration23, Migration24, Migration25,
                    Migration26, Migration27
                )
                .build()
        }
    }

}
