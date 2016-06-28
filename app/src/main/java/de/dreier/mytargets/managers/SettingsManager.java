package de.dreier.mytargets.managers;

import android.content.SharedPreferences;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.activities.EShowMode;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class SettingsManager {
    private static final String KEY_DONATED = "donated";
    private static final String KEY_TIMER_VIBRATE = "timer_vibrate";
    private static final String KEY_TIMER_SOUND = "timer_sound";
    public static final String KEY_TIMER_WARN_TIME = "timer_warn_time";
    public static final String KEY_TIMER_WAIT_TIME = "timer_wait_time";
    public static final String KEY_TIMER_SHOOT_TIME = "timer_shoot_time";
    public static final String KEY_PROFILE_FIRST_NAME = "profile_first_name";
    public static final String KEY_PROFILE_LAST_NAME = "profile_last_name";
    public static final String KEY_PROFILE_BIRTHDAY = "profile_birthday";
    public static final String KEY_PROFILE_CLUB = "profile_club";
    private static final String KEY_STANDARD_ROUND = "standard_round";
    private static final String KEY_ARROW = "arrow";
    private static final String KEY_BOW = "bow";
    private static final String KEY_DISTANCE_VALUE = "distance";
    private static final String KEY_DISTANCE_UNIT = "unit";
    private static final String KEY_ARROWS_PER_PASSE = "ppp";
    private static final String KEY_TARGET = "target";
    private static final String KEY_SCORING_STYLE = "scoring_style";
    private static final String KEY_TARGET_DIAMETER_VALUE = "size_target";
    private static final String KEY_TARGET_DIAMETER_UNIT = "unit_target";
    private static final String KEY_TIMER = "timer";
    private static final String KEY_NUMBERING_ENABLED = "numbering";
    private static final String KEY_INDOOR = "indoor";
    private static final String KEY_PASSES = "rounds";
    private static final String KEY_TRANSLATION_DIALOG_SHOWN = "translation_dialog_shown";
    private static final String KEY_FILTER_CLUB = "filter_club";
    private static final String KEY_INPUT_MODE = "target_mode";
    private static final String KEY_SHOW_MODE = "show_mode";
    private static final SharedPreferences preferences = ApplicationInstance
            .getLastSharedPreferences();

    public static int getStandardRound() {
        return preferences.getInt(KEY_STANDARD_ROUND, 32);
    }

    public static void setStandardRound(long id) {
        preferences.edit()
                .putInt(KEY_STANDARD_ROUND, (int) id)
                .apply();
    }

    public static int getArrow() {
        return preferences.getInt(KEY_ARROW, -1);
    }

    public static void setArrow(long id) {
        preferences.edit()
                .putInt(KEY_ARROW, (int) id)
                .apply();
    }

    public static int getBow() {
        return preferences.getInt(KEY_BOW, -1);
    }

    public static void setBow(long id) {
        preferences.edit()
                .putInt(KEY_BOW, (int) id)
                .apply();
    }

    public static Dimension getDistance() {
        int distance = preferences.getInt(KEY_DISTANCE_VALUE, 10);
        String unit = preferences.getString(KEY_DISTANCE_UNIT, "m");
        return new Dimension(distance, unit);
    }

    public static void setDistance(Dimension distance) {
        preferences.edit()
                .putInt(KEY_DISTANCE_VALUE, distance.value)
                .putString(KEY_DISTANCE_UNIT, distance.unit.toString())
                .apply();
    }

    public static int getArrowsPerPasse() {
        return preferences.getInt(KEY_ARROWS_PER_PASSE, 3);
    }

    public static void setArrowsPerPasse(int arrowsPerPasse) {
        preferences.edit()
                .putInt(KEY_ARROWS_PER_PASSE, arrowsPerPasse)
                .apply();
    }

    public static Target getTarget() {
        final int targetId = preferences.getInt(KEY_TARGET, 0);
        final int scoringStyle = preferences.getInt(KEY_SCORING_STYLE, 0);
        final int diameterValue = preferences.getInt(KEY_TARGET_DIAMETER_VALUE, 60);
        final String diameterUnit = preferences
                .getString(KEY_TARGET_DIAMETER_UNIT, CENTIMETER.toString());
        final Dimension diameter = new Dimension(diameterValue, diameterUnit);
        return new Target(targetId, scoringStyle, diameter);
    }

    public static void setTarget(Target target) {
        preferences.edit()
                .putInt(KEY_TARGET, (int) target.getId())
                .putInt(KEY_SCORING_STYLE, target.scoringStyle)
                .putInt(KEY_TARGET_DIAMETER_VALUE, target.size.value)
                .putString(KEY_TARGET_DIAMETER_UNIT, Dimension.Unit.toStringHandleNull(target.size.unit))
                .apply();
    }

    public static boolean getTimerEnabled() {
        return preferences.getBoolean(KEY_TIMER, false);
    }

    public static void setTimerEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_TIMER, enabled)
                .apply();
    }

    public static boolean getArrowNumbersEnabled() {
        return preferences.getBoolean(KEY_NUMBERING_ENABLED, false);
    }

    public static void setArrowNumbersEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_NUMBERING_ENABLED, enabled)
                .apply();
    }

    public static boolean getIndoor() {
        return preferences.getBoolean(KEY_INDOOR, false);
    }

    public static void setIndoor(boolean indoor) {
        preferences.edit()
                .putBoolean(KEY_INDOOR, indoor)
                .apply();
    }

    public static int getPasses() {
        return preferences.getInt(KEY_PASSES, 10);
    }

    public static void setPasses(int passes) {
        preferences.edit()
                .putInt(KEY_PASSES, passes)
                .apply();
    }

    public static boolean getTranslationDialogWasShown() {
        SharedPreferences prefs = ApplicationInstance.getSharedPreferences();
        return prefs.getBoolean(KEY_TRANSLATION_DIALOG_SHOWN, false);
    }

    public static void setTranslationDialogWasShown(boolean shown) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putBoolean(KEY_TRANSLATION_DIALOG_SHOWN, shown)
                .apply();
    }

    public static int getClubFilter() {
        return ApplicationInstance.getSharedPreferences().getInt(KEY_FILTER_CLUB, 0x1FF);
    }

    public static void setClubFilter(int filter) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putInt(KEY_FILTER_CLUB, filter)
                .apply();
    }

    public static boolean getInputMode() {
        return ApplicationInstance.getSharedPreferences()
                .getBoolean(KEY_INPUT_MODE, false);
    }

    public static void setInputMode(boolean inputMode) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putBoolean(KEY_INPUT_MODE, inputMode)
                .apply();
    }

    public static void setDonated(boolean donated) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putBoolean(KEY_DONATED, donated)
                .apply();
    }

    public static EShowMode getShowMode() {
        return EShowMode.valueOf(ApplicationInstance.getSharedPreferences()
                .getString(KEY_SHOW_MODE, EShowMode.END.toString()));
    }

    public static void setShowMode(EShowMode showMode) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putString(KEY_SHOW_MODE, showMode.toString())
                .apply();
    }

    public static boolean getTimerVibrate() {
        return ApplicationInstance.getSharedPreferences()
                .getBoolean(KEY_TIMER_VIBRATE, false);
    }

    public static boolean getTimerSoundEnabled() {
        return ApplicationInstance.getSharedPreferences()
                .getBoolean(KEY_TIMER_SOUND, true);
    }

    public static int getTimerWaitTime() {
        return getPrefTime(KEY_TIMER_WAIT_TIME, 10);
    }

    public static int getTimerShootTime() {
        return getPrefTime(KEY_TIMER_SHOOT_TIME, 120);
    }

    public static int getTimerWarnTime() {
        return getPrefTime(KEY_TIMER_WARN_TIME, 30);
    }

    private static int getPrefTime(String key, int def) {
        SharedPreferences prefs = ApplicationInstance.getSharedPreferences();
        try {
            return Integer.parseInt(prefs.getString(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getProfileFirstName() {
        return ApplicationInstance.getSharedPreferences()
                .getString(KEY_PROFILE_FIRST_NAME, "");
    }

    public static String getProfileLastName() {
        return ApplicationInstance.getSharedPreferences()
                .getString(KEY_PROFILE_LAST_NAME, "");
    }

    public static String getProfileFullName() {
        return String.format("%s %s", getProfileFirstName(), getProfileLastName());
    }

    public static String getProfileClub() {
        return ApplicationInstance.getSharedPreferences()
                .getString(KEY_PROFILE_CLUB, "");
    }

    private static LocalDate getProfileBirthDay() {
        String date = ApplicationInstance.getSharedPreferences()
                .getString(KEY_PROFILE_BIRTHDAY, "");
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date);
    }

    public static void setProfileBirthDay(LocalDate birthDay) {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putString(KEY_PROFILE_BIRTHDAY, birthDay.toString())
                .apply();
    }

    public static String getProfileBirthDayFormatted() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null)
            return null;
        return DateTimeFormat.mediumDate().print(birthDay);
    }

    public static int getProfileAge() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null) {
            return -1;
        }
        return Years.yearsBetween(birthDay, LocalDate.now()).getYears();
    }
}
