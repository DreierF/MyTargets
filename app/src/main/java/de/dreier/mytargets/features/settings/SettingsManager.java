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

package de.dreier.mytargets.features.settings;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.Map;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.features.settings.backup.EBackupInterval;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.training.input.EShowMode;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.features.training.input.TargetView.EKeyboardType;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class SettingsManager {
    public static final String KEY_TIMER_WARN_TIME = "timer_warn_time";
    public static final String KEY_TIMER_WAIT_TIME = "timer_wait_time";
    public static final String KEY_TIMER_SHOOT_TIME = "timer_shoot_time";
    public static final String KEY_PROFILE_FIRST_NAME = "profile_first_name";
    public static final String KEY_PROFILE_LAST_NAME = "profile_last_name";
    public static final String KEY_PROFILE_BIRTHDAY = "profile_birthday";
    public static final String KEY_PROFILE_CLUB = "profile_club";
    public static final String KEY_INPUT_ARROW_DIAMETER_SCALE = "input_arrow_diameter_scale";
    public static final String KEY_INPUT_TARGET_ZOOM = "input_target_zoom";
    public static final String KEY_INPUT_KEYBOARD_TYPE = "input_keyboard_type";
    public static final String KEY_FIRST_TRAINING_SHOWN = "first_training_shown";
    private static final String KEY_BACKUP_INTERVAL = "backup_interval";
    private static final String KEY_DONATED = "donated";
    private static final String KEY_TIMER_VIBRATE = "timer_vibrate";
    private static final String KEY_TIMER_SOUND = "timer_sound";
    private static final String KEY_STANDARD_ROUND = "standard_round";
    private static final String KEY_ARROW = "arrow";
    private static final String KEY_BOW = "bow";
    private static final String KEY_DISTANCE_VALUE = "distance";
    private static final String KEY_DISTANCE_UNIT = "unit";
    private static final String KEY_ARROWS_PER_END = "ppp";
    private static final String KEY_TARGET = "target";
    private static final String KEY_SCORING_STYLE = "scoring_style";
    private static final String KEY_TARGET_DIAMETER_VALUE = "size_target";
    private static final String KEY_TARGET_DIAMETER_UNIT = "unit_target";
    private static final String KEY_TIMER = "timer";
    private static final String KEY_NUMBERING_ENABLED = "numbering";
    private static final String KEY_INDOOR = "indoor";
    private static final String KEY_END_COUNT = "rounds";
    private static final String KEY_TRANSLATION_DIALOG_SHOWN = "translation_dialog_shown";
    private static final String KEY_INPUT_MODE = "target_mode";
    private static final String KEY_SHOW_MODE = "show_mode";
    private static final SharedPreferences lastUsed = ApplicationInstance
            .getLastSharedPreferences();
    private static final SharedPreferences preferences = ApplicationInstance
            .getSharedPreferences();
    private static final String KEY_BACKUP_LOCATION = "backup_location";
    private static final String KEY_AGGREGATION_STRATEGY = "aggregation_strategy";
    private static final String KEY_BACKUP_AUTOMATICALLY = "backup_automatically";
    private static final String KEY_STANDARD_ROUNDS_LAST_USED = "standard_round_last_used";
    private static final String KEY_INTRO_SHOWED = "intro_showed";

    public static int getStandardRound() {
        return lastUsed.getInt(KEY_STANDARD_ROUND, 32);
    }

    public static void setStandardRound(long id) {
        lastUsed.edit()
                .putInt(KEY_STANDARD_ROUND, (int) id)
                .apply();
    }

    @Nullable
    public static Long getArrow() {
        final int arrow = lastUsed.getInt(KEY_ARROW, -1);
        return arrow <= 0 ? null : (long) arrow;
    }

    public static void setArrow(@Nullable Long id) {
        lastUsed.edit()
                .putInt(KEY_ARROW, id == null ? -1 : (int) (long) id)
                .apply();
    }

    @Nullable
    public static Long getBow() {
        final int bow = lastUsed.getInt(KEY_BOW, -1);
        return bow <= 0 ? null : (long) bow;
    }

    public static void setBow(@Nullable Long id) {
        lastUsed.edit()
                .putInt(KEY_BOW, id == null ? -1 : (int) (long) id)
                .apply();
    }

    public static Dimension getDistance() {
        int distance = lastUsed.getInt(KEY_DISTANCE_VALUE, 10);
        String unit = lastUsed.getString(KEY_DISTANCE_UNIT, "m");
        return new Dimension(distance, unit);
    }

    public static void setDistance(Dimension distance) {
        lastUsed.edit()
                .putInt(KEY_DISTANCE_VALUE, (int) distance.value)
                .putString(KEY_DISTANCE_UNIT, Dimension.Unit.toStringHandleNull(distance.unit))
                .apply();
    }

    public static int getShotsPerEnd() {
        return lastUsed.getInt(KEY_ARROWS_PER_END, 3);
    }

    public static void setShotsPerEnd(int shotsPerEnd) {
        lastUsed.edit()
                .putInt(KEY_ARROWS_PER_END, shotsPerEnd)
                .apply();
    }

    public static Target getTarget() {
        final int targetId = lastUsed.getInt(KEY_TARGET, 0);
        final int scoringStyle = lastUsed.getInt(KEY_SCORING_STYLE, 0);
        final int diameterValue = lastUsed.getInt(KEY_TARGET_DIAMETER_VALUE, 60);
        final String diameterUnit = lastUsed
                .getString(KEY_TARGET_DIAMETER_UNIT, CENTIMETER.toString());
        final Dimension diameter = new Dimension(diameterValue, diameterUnit);
        return new Target(targetId, scoringStyle, diameter);
    }

    public static void setTarget(Target target) {
        lastUsed.edit()
                .putInt(KEY_TARGET, (int) (long) target.getId())
                .putInt(KEY_SCORING_STYLE, target.scoringStyle)
                .putInt(KEY_TARGET_DIAMETER_VALUE, (int) target.size.value)
                .putString(KEY_TARGET_DIAMETER_UNIT,
                        Dimension.Unit.toStringHandleNull(target.size.unit))
                .apply();
    }

    public static boolean getTimerEnabled() {
        return lastUsed.getBoolean(KEY_TIMER, false);
    }

    public static void setTimerEnabled(boolean enabled) {
        lastUsed.edit()
                .putBoolean(KEY_TIMER, enabled)
                .apply();
    }

    public static boolean getArrowNumbersEnabled() {
        return lastUsed.getBoolean(KEY_NUMBERING_ENABLED, true);
    }

    public static void setArrowNumbersEnabled(boolean enabled) {
        lastUsed.edit()
                .putBoolean(KEY_NUMBERING_ENABLED, enabled)
                .apply();
    }

    public static boolean getIndoor() {
        return lastUsed.getBoolean(KEY_INDOOR, false);
    }

    public static void setIndoor(boolean indoor) {
        lastUsed.edit()
                .putBoolean(KEY_INDOOR, indoor)
                .apply();
    }

    public static int getEndCount() {
        return lastUsed.getInt(KEY_END_COUNT, 10);
    }

    public static void setEndCount(int endCount) {
        lastUsed.edit()
                .putInt(KEY_END_COUNT, endCount)
                .apply();
    }

    public static boolean isTranslationDialogShown() {
        SharedPreferences prefs = preferences;
        return prefs.getBoolean(KEY_TRANSLATION_DIALOG_SHOWN, false);
    }

    public static void setTranslationDialogShown(boolean shown) {
        preferences
                .edit()
                .putBoolean(KEY_TRANSLATION_DIALOG_SHOWN, shown)
                .apply();
    }

    public static TargetViewBase.EInputMethod getInputMethod() {
        return preferences
                .getBoolean(KEY_INPUT_MODE, false)
                ? TargetViewBase.EInputMethod.KEYBOARD
                : TargetViewBase.EInputMethod.PLOTTING;
    }

    public static void setInputMethod(TargetViewBase.EInputMethod inputMethod) {
        preferences
                .edit()
                .putBoolean(KEY_INPUT_MODE, inputMethod == TargetViewBase.EInputMethod.KEYBOARD)
                .apply();
    }

    public static boolean hasDonated() {
        return preferences
                .getBoolean(KEY_DONATED, false);
    }

    public static void setDonated(boolean donated) {
        preferences
                .edit()
                .putBoolean(KEY_DONATED, donated)
                .apply();
    }

    public static EShowMode getShowMode() {
        return EShowMode.valueOf(preferences
                .getString(KEY_SHOW_MODE, EShowMode.END.toString()));
    }

    public static void setShowMode(EShowMode showMode) {
        preferences
                .edit()
                .putString(KEY_SHOW_MODE, showMode.toString())
                .apply();
    }

    public static EAggregationStrategy getAggregationStrategy() {
        return EAggregationStrategy.valueOf(preferences
                .getString(KEY_AGGREGATION_STRATEGY, EAggregationStrategy.AVERAGE.toString()));
    }

    public static void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        preferences
                .edit()
                .putString(KEY_AGGREGATION_STRATEGY, aggregationStrategy.toString())
                .apply();
    }

    public static boolean getTimerVibrate() {
        return preferences
                .getBoolean(KEY_TIMER_VIBRATE, false);
    }

    public static void setTimerVibrate(boolean vibrate) {
        preferences
                .edit()
                .putBoolean(KEY_TIMER_VIBRATE, vibrate)
                .apply();
    }

    public static boolean getTimerSoundEnabled() {
        return preferences
                .getBoolean(KEY_TIMER_SOUND, true);
    }

    public static void setTimerSoundEnabled(boolean soundEnabled) {
        preferences
                .edit()
                .putBoolean(KEY_TIMER_SOUND, soundEnabled)
                .apply();
    }

    public static int getTimerWaitTime() {
        return getPrefTime(KEY_TIMER_WAIT_TIME, 10);
    }

    public static void setTimerWaitTime(int waitTime) {
        preferences
                .edit()
                .putString(KEY_TIMER_WAIT_TIME, String.valueOf(waitTime))
                .apply();
    }

    public static int getTimerShootTime() {
        return getPrefTime(KEY_TIMER_SHOOT_TIME, 120);
    }

    public static void setTimerShootTime(int shootTime) {
        preferences
                .edit()
                .putString(KEY_TIMER_SHOOT_TIME, String.valueOf(shootTime))
                .apply();
    }

    public static int getTimerWarnTime() {
        return getPrefTime(KEY_TIMER_WARN_TIME, 30);
    }

    public static void setTimerWarnTime(int warnTime) {
        preferences
                .edit()
                .putString(KEY_TIMER_WARN_TIME, String.valueOf(warnTime))
                .apply();
    }

    private static int getPrefTime(String key, int def) {
        try {
            return Integer.parseInt(preferences.getString(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getProfileFirstName() {
        return preferences
                .getString(KEY_PROFILE_FIRST_NAME, "");
    }

    public static void setProfileFirstName(String firstName) {
        preferences
                .edit()
                .putString(KEY_PROFILE_FIRST_NAME, firstName)
                .apply();
    }

    public static String getProfileLastName() {
        return preferences
                .getString(KEY_PROFILE_LAST_NAME, "");
    }

    public static void setProfileLastName(String lastName) {
        preferences
                .edit()
                .putString(KEY_PROFILE_LAST_NAME, lastName)
                .apply();
    }

    public static String getProfileFullName() {
        return String.format("%s %s", getProfileFirstName(), getProfileLastName());
    }

    public static String getProfileClub() {
        return preferences
                .getString(KEY_PROFILE_CLUB, "");
    }

    public static void setProfileClub(String club) {
        preferences
                .edit()
                .putString(KEY_PROFILE_CLUB, club)
                .apply();
    }

    public static LocalDate getProfileBirthDay() {
        String date = preferences
                .getString(KEY_PROFILE_BIRTHDAY, "");
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date);
    }

    public static void setProfileBirthDay(LocalDate birthDay) {
        preferences.edit()
                .putString(KEY_PROFILE_BIRTHDAY, birthDay.toString())
                .apply();
    }

    public static String getProfileBirthDayFormatted() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null) {
            return null;
        }
        return DateTimeFormat.mediumDate().print(birthDay);
    }

    public static int getProfileAge() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null) {
            return -1;
        }
        return Years.yearsBetween(birthDay, LocalDate.now()).getYears();
    }

    public static float getInputArrowDiameterScale() {
        return Float.parseFloat(preferences
                .getString(KEY_INPUT_ARROW_DIAMETER_SCALE, "1.0"));
    }

    public static void setInputArrowDiameterScale(float diameterScale) {
        preferences.edit()
                .putString(KEY_INPUT_ARROW_DIAMETER_SCALE, String.valueOf(diameterScale))
                .apply();
    }

    public static float getInputTargetZoom() {
        return Float.parseFloat(preferences
                .getString(KEY_INPUT_TARGET_ZOOM, "3.0"));
    }

    public static void setInputTargetZoom(float targetZoom) {
        preferences.edit()
                .putString(KEY_INPUT_TARGET_ZOOM, String.valueOf(targetZoom))
                .apply();
    }

    public static EKeyboardType getInputKeyboardType() {
        return EKeyboardType.valueOf(preferences
                .getString(KEY_INPUT_KEYBOARD_TYPE, EKeyboardType.RIGHT.name()));
    }

    public static void setInputKeyboardType(EKeyboardType type) {
        preferences
                .edit()
                .putString(KEY_INPUT_KEYBOARD_TYPE, type.name())
                .apply();
    }

    public static boolean isFirstTrainingShown() {
        return ApplicationInstance.getSharedPreferences()
                .getBoolean(KEY_FIRST_TRAINING_SHOWN, false);
    }

    public static void setFirstTrainingShown(boolean shown) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putBoolean(KEY_FIRST_TRAINING_SHOWN, shown)
                .apply();
    }

    public static EBackupLocation getBackupLocation() {
        final String defaultLocation = EBackupLocation.INTERNAL_STORAGE.name();
        String location = preferences.getString(KEY_BACKUP_LOCATION, defaultLocation);
        return EBackupLocation.valueOf(location);
    }

    public static void setBackupLocation(EBackupLocation location) {
        preferences
                .edit()
                .putString(KEY_BACKUP_LOCATION, location.name())
                .apply();
    }

    public static EBackupInterval getBackupInterval() {
        return EBackupInterval.valueOf(preferences.getString(KEY_BACKUP_INTERVAL, "WEEKLY"));
    }

    public static void setBackupInterval(EBackupInterval interval) {
        preferences.edit()
                .putString(KEY_BACKUP_INTERVAL, interval.name())
                .apply();
    }

    public static boolean isBackupAutomaticallyEnabled() {
        return preferences.getBoolean(KEY_BACKUP_AUTOMATICALLY, false);
    }

    public static void setBackupAutomaticallyEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_BACKUP_AUTOMATICALLY, enabled)
                .apply();
    }

    public static Map<Long, Integer> getStandardRoundsLastUsed() {
        String[] split = lastUsed.getString(KEY_STANDARD_ROUNDS_LAST_USED, "").split(",");
        return Stream.of(Arrays.asList(split))
                .filterNot(String::isEmpty)
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(a -> Long.valueOf(a[0]), a -> Integer.valueOf(a[1])));
    }

    public static void setStandardRoundsLastUsed(Map<Long, Integer> ids) {
        lastUsed.edit()
                .putString(KEY_STANDARD_ROUNDS_LAST_USED, Stream.of(ids)
                        .map(id -> id.getKey() + ":" + id.getValue())
                        .collect(Collectors.joining(",")))
                .apply();
    }

    public static boolean shouldShowIntroActivity() {
        return preferences
                .getBoolean(KEY_INTRO_SHOWED, true);
    }

    public static void setShouldShowIntroActivity(boolean shouldShow) {
        preferences.edit()
                .putBoolean(KEY_INTRO_SHOWED, shouldShow)
                .apply();
    }
}
