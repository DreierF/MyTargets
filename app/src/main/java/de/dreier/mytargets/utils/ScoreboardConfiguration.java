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

import android.content.SharedPreferences;

import de.dreier.mytargets.ApplicationInstance;

public class ScoreboardConfiguration {
    boolean showTitle;
    boolean showProperties;
    boolean showTable;
    boolean showStatistics;
    boolean showComments;
    boolean showPointsColored;
    boolean showSignature;

    private ScoreboardConfiguration() {
    }

    //TODO Use default values from xml
    public static ScoreboardConfiguration fromDisplaySettings() {
        return getFromSettingsForPrefix("scoreboard_display_", true, true, true, true, true, true, false);
    }

    public static ScoreboardConfiguration fromPrintSettings() {
        return getFromSettingsForPrefix("scoreboard_print_", true, true, true, true, true, false, true);
    }

    static ScoreboardConfiguration fromShareSettings() {
        return getFromSettingsForPrefix("scoreboard_share_", true, true, true, true, true, true, false);
    }

    private static ScoreboardConfiguration getFromSettingsForPrefix(String prefix,
                                                                    boolean title, boolean properties,
                                                                    boolean table, boolean statistics, boolean comments,
                                                                    boolean pointsColored, boolean signature) {
        SharedPreferences prefs = ApplicationInstance.getSharedPreferences();
        ScoreboardConfiguration config = new ScoreboardConfiguration();
        config.showTitle = prefs.getBoolean(prefix + "title", title);
        config.showProperties = prefs.getBoolean(prefix + "properties", properties);
        config.showTable = prefs.getBoolean(prefix + "table", table);
        config.showStatistics = prefs.getBoolean(prefix + "statistics", statistics);
        config.showComments = prefs.getBoolean(prefix + "comments", comments);
        config.showPointsColored = prefs.getBoolean(prefix + "points_colored", pointsColored);
        config.showSignature = prefs.getBoolean(prefix + "signature", signature);
        return config;
    }
}
