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

package de.dreier.mytargets.shared.streamwrapper

import de.dreier.mytargets.shared.models.Score
import java.util.*


class Stream<T>(private val list: Iterable<T>) {
    fun <R> map(function: (T) -> R): Stream<R> {
        return Stream(list.map(function))
    }

    fun distinct(): Stream<T> {
        return Stream(list.distinct())
    }

    fun filter(predicate: (T) -> Boolean): Stream<T> {
        return Stream(list.filter(predicate))
    }

    fun <S> flatMap(func: (T) -> Stream<S>): Stream<S> {
        return Stream(list.flatMap {
            func(it).list
        })
    }

    fun sorted(): Stream<T> {
        return Stream(list.toList().sortedWith(compareBy { if (it is Comparable<*>) it else null }))
    }

    fun sorted(comparator: Comparator<T>): Stream<T> {
        return Stream(list.toList().sortedWith(comparator))
    }

    fun toList(): ArrayList<T> {
        return ArrayList(list.toMutableList())
    }

    fun scoreSum(): Score {
        return list.fold(Score()) { score, s ->
            score.add(s as Score)
        }
    }

    fun joining(sep: String): String {
        return list.joinToString(sep)
    }

    fun count(): Int = list.count()

    fun toSet(): Set<T> = list.toSet()

    fun anyMatch(predicate: (T) -> Boolean): Boolean {
        return list.any(predicate)
    }

    fun allMatch(predicate: (T) -> Boolean): Boolean {
        return list.all(predicate)
    }

    fun findFirstOrNull(): T? {
        return list.firstOrNull()
    }

    fun min(comp: Comparator<T>): T? {
        return list.minWith(comp)
    }

    companion object {
        @JvmStatic
        fun rangeClosed(from: Int, to: Int): Stream<Int> {
            return Stream(from..to)
        }

        @JvmStatic
        fun <T> of(vararg v: T): Stream<T> {
            return Stream(v.toList())
        }

        @JvmStatic
        fun <T> of(iterable: Iterable<T>): Stream<T> {
            return Stream(iterable)
        }

        @JvmStatic
        fun <S, T> of(map: Map<S, T>): Stream<Map.Entry<S, T>> {
            return Stream(map.entries)
        }
    }
}
