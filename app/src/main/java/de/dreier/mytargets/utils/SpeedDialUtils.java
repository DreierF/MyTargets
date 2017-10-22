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

package de.dreier.mytargets.utils;

import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;

import com.annimon.stream.Stream;

import java.lang.reflect.Field;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class SpeedDialUtils {
    public static FloatingActionButton getFabFromMenuId(FabSpeedDial speedDial, int id) {
        try {
            Field fabMenuItemMap = speedDial.getClass()
                    .getDeclaredField("fabMenuItemMap");
            fabMenuItemMap.setAccessible(true);
            Map<FloatingActionButton, MenuItem> floatingActionButtonMenuItemMap = (Map<FloatingActionButton, MenuItem>) fabMenuItemMap
                    .get(speedDial);
            return Stream.of(floatingActionButtonMenuItemMap.entrySet())
                    .filter(entry -> entry.getValue().getItemId() == id)
                    .map(Map.Entry::getKey).findFirst().orElse(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
