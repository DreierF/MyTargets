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

package de.dreier.mytargets.utils

import androidx.collection.LongSparseArray

inline fun <reified T> List<Pair<Long, T>>.toSparseArray(): LongSparseArray<T> {
    val array = LongSparseArray<T>(size)
    for ((key, value) in this) {
        array.put(key, value)
    }
    return array
}

inline fun <reified T, R> LongSparseArray<T>.map(transform: (Pair<Long, T>) -> R): List<R> {
    val list = ArrayList<R>(size())
    for (i in 0 until size()) {
        val key = keyAt(i)
        val value = get(key)!!
        list.add(transform.invoke(Pair(key, value)))
    }
    return list
}

fun <T> LongSparseArray<T>.contains(key: Long): Boolean {
    return indexOfKey(key) >= 0
}
