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
package de.dreier.mytargets.features.settings

import android.content.SharedPreferences
import androidx.content.edit

operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        null -> edit { remove(key) }
        is String? -> edit { putString(key, value) }
        is Int -> edit { putInt(key, value) }
        is Boolean -> edit { putBoolean(key, value) }
        is Float -> edit { putFloat(key, value) }
        is Long -> edit { putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline operator fun <reified T : Any> SharedPreferences.get(key: String): T? {
    if (!contains(key)) {
        return null
    }
    return when (T::class) {
        String::class -> getString(key, null) as T?
        Int::class -> {
            val value = getInt(key, -1) as T?
            if (value == -1) null else value
        }
        Boolean::class -> getBoolean(key, false) as T?
        Float::class -> {
            val value = getFloat(key, -1f) as T?
            if (value == -1f) null else value
        }
        Long::class -> {
            val value = getLong(key, -1) as T?
            if (value == -1) null else value
        }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
    if (!contains(key)) {
        return defaultValue
    }
    return when (T::class) {
        String::class -> getString(key, defaultValue as String) as T
        Int::class -> getInt(key, defaultValue as Int) as T
        Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
        Float::class -> getFloat(key, defaultValue as Float) as T
        Long::class -> getLong(key, defaultValue as Long) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}
