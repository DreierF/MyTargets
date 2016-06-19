package de.dreier.mytargets.utils;

import android.content.SharedPreferences;

import de.dreier.mytargets.ApplicationInstance;

public class ScoreboardConfiguration {
    boolean showTitle;
    boolean showProperties;
    boolean showTable;
    boolean showComments;
    boolean showPointsColored;
    boolean showSignature;

    private ScoreboardConfiguration() {
    }

    //TODO Use default values from xml
    public static ScoreboardConfiguration fromDisplaySettings() {
        return getFromSettingsForPrefix("scoreboard_display_", true, true, true, true, true, false);
    }

    public static ScoreboardConfiguration fromPrintSettings() {
        return getFromSettingsForPrefix("scoreboard_print_", true, true, true, true, false, true);
    }

    static ScoreboardConfiguration fromShareSettings() {
        return getFromSettingsForPrefix("scoreboard_share_", true, true, true, true, true, false);
    }

    private static ScoreboardConfiguration getFromSettingsForPrefix(String prefix,
                                                                    boolean title, boolean properties,
                                                                    boolean table, boolean comments,
                                                                    boolean pointsColored, boolean signature) {
        SharedPreferences prefs = ApplicationInstance.getSharedPreferences();
        ScoreboardConfiguration config = new ScoreboardConfiguration();
        config.showTitle = prefs.getBoolean(prefix + "title", title);
        config.showProperties = prefs.getBoolean(prefix + "properties", properties);
        config.showTable = prefs.getBoolean(prefix + "table", table);
        config.showComments = prefs.getBoolean(prefix + "comments", comments);
        config.showPointsColored = prefs.getBoolean(prefix + "points_colored", pointsColored);
        config.showSignature = prefs.getBoolean(prefix + "signature", signature);
        return config;
    }
}
