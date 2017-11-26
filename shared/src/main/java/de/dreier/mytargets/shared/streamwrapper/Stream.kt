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

    fun <R> reducing(initial: R, operation: (acc: R, T) -> R): R {
        return list.fold(initial, operation)
    }

    fun <R> reduce(initial: R, operation: (acc: R, T) -> R): R {
        return list.fold(initial, operation)
    }

    fun filter(predicate: (T) -> Boolean): Stream<T> {
        return Stream(list.filter(predicate))
    }

    fun filterNot(predicate: (T) -> Boolean): Stream<T> {
        return Stream(list.filterNot(predicate))
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

    fun toList(): List<T> {
        return list.toMutableList()
    }

    fun <U, V> toMap(key: (T) -> U, value: (T) -> V): Map<U, V> {
        return list.map { Pair(key(it), value(it)) }.toMap()
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

    fun <U> groupingBy(group: (T) -> U): Map<U, List<T>> {
        return list.groupBy { group(it) }
    }

    fun <U> groupBy(group: (T) -> U): Stream<Map.Entry<U, List<T>>> {
        return Stream.of(list.groupBy { group(it) })
    }

    fun <U : Comparable<U>> sortBy(value: (T) -> U): Stream<T> {
        return Stream(list.sortedBy(value))
    }

    fun anyMatch(predicate: (T) -> Boolean): Boolean {
        return list.any(predicate)
    }

    fun allMatch(predicate: (T) -> Boolean): Boolean {
        return list.all(predicate)
    }

    fun findFirst(elseValue: T): T {
        return list.firstOrNull() ?: elseValue
    }

    fun findFirstOrNull(): T? {
        return list.firstOrNull()
    }

    fun min(comp: Comparator<T>): T? {
        return list.minWith(comp)
    }

    fun forEach(l: (T) -> Unit) {
        list.forEach(l)
    }

    fun withoutNulls(): Stream<T> {
        return Stream(list.filterNot { it == null })
    }

    fun toArray(a: (Int)->Array<T>): Array<T> {
        return ArrayList(list.toList()).toArray(a(list.count()))
    }

    companion object {
//        @JvmStatic
//        fun <T> of(iterable: Array<T>): Stream<T> {
//            return Stream(iterable.toList())
//        }


        @JvmStatic
        fun rangeClosed(from: Int, to: Int): Stream<Int> {
            return Stream(from..to)
        }

        @JvmStatic
        fun <T> zip(a1: Array<T>, a2: Array<T>): Stream<Pair<T, T>> {
            return Stream(a1.zip(a2))
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
