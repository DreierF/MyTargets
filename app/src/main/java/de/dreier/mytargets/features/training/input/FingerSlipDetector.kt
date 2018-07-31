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

package de.dreier.mytargets.features.training.input

import android.graphics.PointF
import java.util.*

/**
 * Detects finger slipping while releasing the finger from the screen.
 * This simply ignores the positions recorded during the last 100ms.
 */
internal class FingerSlipDetector {

    private val cache = Stack<TimedPoint>()
    private val list = LinkedList<TimedPoint>()
    private var currentMaturePosition: TimedPoint? = null

    val finalPosition: PointF?
        get() = if (currentMaturePosition == null) {
            null
        } else PointF(currentMaturePosition!!.x, currentMaturePosition!!.y)

    init {
        for (i in 0 until INITIAL_CACHE_SIZE) {
            cache.push(TimedPoint())
        }
    }

    fun addShot(x: Float, y: Float) {
        if (cache.isEmpty()) {
            cache.push(TimedPoint())
        }
        val point = cache.pop()
        point.time = System.currentTimeMillis()
        point.x = x
        point.y = y
        if (currentMaturePosition == null) {
            currentMaturePosition = point
        } else {
            list.add(point)
        }
        while (!list.isEmpty() && list.first.time < point.time - TIME_WINDOW) {
            cache.push(currentMaturePosition)
            currentMaturePosition = list.removeFirst()
        }
    }

    fun reset() {
        if (currentMaturePosition == null) {
            return
        }

        cache.push(currentMaturePosition)
        currentMaturePosition = null
        cache.addAll(list)
        list.clear()
    }

    private inner class TimedPoint {
        internal var time: Long = 0
        internal var x: Float = 0f
        internal var y: Float = 0f
    }

    companion object {
        private const val TIME_WINDOW = 100L // in milliseconds
        private const val INITIAL_CACHE_SIZE = 20
    }
}
