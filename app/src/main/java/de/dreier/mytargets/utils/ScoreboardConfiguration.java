package de.dreier.mytargets.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ScoreboardConfiguration {
    boolean showTitle;
    boolean showProperties;
    boolean showTable;
    boolean showComments;
    boolean showDispersionPattern;
    boolean showPointsColored;

    private ScoreboardConfiguration() {
    }

    public static ScoreboardConfiguration fromDisplaySettings(Context context) {
        return getFromSettingsForPrefix(context, "scoreboard_print_", true, true, true, true, true);
    }

    public static ScoreboardConfiguration fromPrintSettings(Context context) {
        return getFromSettingsForPrefix(context, "scoreboard_print_", true, true, true, true, false);
    }

    public static ScoreboardConfiguration fromShareSettings(Context context) {
        return getFromSettingsForPrefix(context, "scoreboard_share_", false, false, true, false, false);
    }

    private static ScoreboardConfiguration getFromSettingsForPrefix(Context context, String prefix,
                                                                    boolean title, boolean properties,
                                                                    boolean table, boolean comments,
                                                                    boolean dispersionPattern) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ScoreboardConfiguration config = new ScoreboardConfiguration();
        config.showTitle = prefs.getBoolean(prefix + "title", title);
        config.showProperties = prefs.getBoolean(prefix + "properties", properties);
        config.showTable = prefs.getBoolean(prefix + "table", table);
        config.showComments = prefs.getBoolean(prefix + "comments", comments);
        config.showDispersionPattern = prefs.getBoolean(prefix + "dispersion_pattern", dispersionPattern);
        config.showPointsColored = prefs.getBoolean(prefix + "points_colored", dispersionPattern);
        return config;
    }
}
