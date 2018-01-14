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

package de.dreier.mytargets.shared

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.StringRes
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

open class SharedApplicationInstance : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        context = applicationContext
    }

    protected fun enableDebugLogging() {
//        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyLog()
//                .build())
//        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .build())
        Timber.plant(Timber.DebugTree())
    }

    companion object {

        lateinit var context: Context
            protected set

        fun getStr(@StringRes string: Int): String {
            return context.getString(string)
        }

        val sharedPreferences: SharedPreferences
            get() {
                return PreferenceManager.getDefaultSharedPreferences(context)
            }
    }
}
