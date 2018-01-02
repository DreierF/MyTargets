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

package de.dreier.mytargets.shared.utils

import android.support.annotation.ColorInt

object Color {
    @ColorInt
    const val SAPPHIRE_BLUE = -0xd1b761
    @ColorInt
    const val DARK_GRAY = -0xdde0e1
    @ColorInt
    const val GRAY = -0x979798
    @ColorInt
    const val LIGHTER_GRAY = -0x484849
    @ColorInt
    const val LIGHT_GRAY = -0x242426
    @ColorInt
    const val ORANGE = -0x599d
    @ColorInt
    const val GREEN = -0xff60dd
    @ColorInt
    const val BROWN = -0x608800
    @ColorInt
    const val CERULEAN_BLUE = -0xff5211
    @ColorInt
    const val FLAMINGO_RED = -0x10b1b4
    @ColorInt
    const val RED = -0xfff3
    @ColorInt
    const val TURBO_YELLOW = -0x11600
    @ColorInt
    const val LEMON_YELLOW = -0x914f1
    @ColorInt
    const val RED_MISS = -0x11c2ca
    @ColorInt
    const val YELLOW = -0x14ae
    @ColorInt
    const val BLACK = -0x1000000
    @ColorInt
    const val WHITE = -0x1
    @ColorInt
    const val DBSC_RED = -0x25dae4
    @ColorInt
    const val DBSC_YELLOW = -0xb00
    @ColorInt
    const val DBSC_BLUE = -0x8a3a10

    fun getStrokeColor(@ColorInt fillColor: Int): Int {
        return when (fillColor) {
            WHITE -> BLACK
            BLACK, DARK_GRAY,
            GRAY, LIGHT_GRAY,
            ORANGE, GREEN,
            BROWN, CERULEAN_BLUE,
            SAPPHIRE_BLUE, FLAMINGO_RED,
            RED, TURBO_YELLOW,
            LEMON_YELLOW, DBSC_BLUE,
            DBSC_RED, DBSC_YELLOW -> fillColor
            else -> DARK_GRAY
        }
    }

    fun getContrast(fillColor: Int): Int {
        return when (fillColor) {
            WHITE, LIGHTER_GRAY,
            LIGHT_GRAY, TURBO_YELLOW,
            LEMON_YELLOW, YELLOW,
            DBSC_YELLOW, ORANGE,
            GREEN, BROWN -> BLACK
            else -> WHITE
        }
    }
}
