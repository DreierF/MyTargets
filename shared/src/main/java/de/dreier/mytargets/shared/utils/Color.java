/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.shared.utils;

import android.support.annotation.ColorInt;

public class Color {
    @ColorInt
    public static final int SAPPHIRE_BLUE = 0xFF2E489F;
    @ColorInt
    public static final int DARK_GRAY = 0xFF221F1F;
    @ColorInt
    public static final int GRAY = 0xFF686868;
    @ColorInt
    public static final int LIGHTER_GRAY = 0xFFB7B7B7;
    @ColorInt
    public static final int LIGHT_GRAY = 0xFFDBDBDA;
    @ColorInt
    public static final int ORANGE = 0xFFFFA663;
    @ColorInt
    public static final int GREEN = 0xFF009F23;
    @ColorInt
    public static final int BROWN = 0xFF9F7800;
    @ColorInt
    public static final int CERULEAN_BLUE = 0xFF00ADEF;
    @ColorInt
    public static final int FLAMINGO_RED = 0xFFEF4E4C;
    @ColorInt
    public static final int RED = 0xFFFF000D;
    @ColorInt
    public static final int TURBO_YELLOW = 0xFFFEEA00;
    @ColorInt
    public static final int LEMON_YELLOW = 0xFFF6EB0F;
    @ColorInt
    public static final int RED_MISS = 0xFFEE3D36;
    @ColorInt
    public static final int YELLOW = 0xFFFFEB52;
    @ColorInt
    public static final int BLACK = 0xFF000000;
    @ColorInt
    public static final int WHITE = 0xFFFFFFFF;
    @ColorInt
    public static final int DBSC_RED = 0xFFDA251C;
    @ColorInt
    public static final int DBSC_YELLOW = 0xFFFFF500;
    @ColorInt
    public static final int DBSC_BLUE = 0xFF75C5F0;

    public static int getStrokeColor(@ColorInt int fillColor) {
        switch (fillColor) {
            case WHITE:
                return BLACK;
            case BLACK:
            case DARK_GRAY:
            case GRAY:
            case LIGHT_GRAY:
            case ORANGE:
            case GREEN:
            case BROWN:
            case CERULEAN_BLUE:
            case SAPPHIRE_BLUE:
            case FLAMINGO_RED:
            case RED:
            case TURBO_YELLOW:
            case LEMON_YELLOW:
            case DBSC_BLUE:
            case DBSC_RED:
            case DBSC_YELLOW:
                return fillColor;
            default:
                return DARK_GRAY;
        }
    }

    public static int getContrast(int fillColor) {
        switch (fillColor) {
            case WHITE:
            case LIGHTER_GRAY:
            case LIGHT_GRAY:
            case TURBO_YELLOW:
            case LEMON_YELLOW:
            case YELLOW:
            case DBSC_YELLOW:
                return BLACK;
            case ORANGE:
                return BLACK;
            case GREEN:
                return BLACK;
            case BROWN:
                return BLACK;
            default:
                return WHITE;
        }
    }
}
