package de.dreier.mytargets.managers;

import android.content.SharedPreferences;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.backup.EBackupLocation;
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;
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
    public static final String KEY_BACKUP_INTERVAL = "backup_interval";
    private static final String KEY_DONATED = "donated";
    private static final String KEY_TIMER_VIBRATE = "timer_vibrate";
    private static final String KEY_TIMER_SOUND = "timer_sound";
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
    private static final String KEY_INPUT_MODE = "target_mode";
    private static final String KEY_SHOW_MODE = "show_mode";
    private static final SharedPreferences lastUsed = ApplicationInstance
            .getLastSharedPreferences();
    private static final SharedPreferences preferences = ApplicationInstance
            .getSharedPreferences();
    private static final String KEY_BACKUP_LOCATION = "backup_location";

    public static int getStandardRound() {
        return lastUsed.getInt(KEY_STANDARD_ROUND, 32);
    }

    public static void setStandardRound(long id) {
        lastUsed.edit()
                .putInt(KEY_STANDARD_ROUND, (int) id)
                .apply();
    }

    public static int getArrow() {
        return lastUsed.getInt(KEY_ARROW, -1);
    }

    public static void setArrow(long id) {
        lastUsed.edit()
                .putInt(KEY_ARROW, (int) id)
                .apply();
    }

    public static int getBow() {
        return lastUsed.getInt(KEY_BOW, -1);
    }

    public static void setBow(long id) {
        lastUsed.edit()
                .putInt(KEY_BOW, (int) id)
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

    public static int getArrowsPerPasse() {
        return lastUsed.getInt(KEY_ARROWS_PER_PASSE, 3);
    }

    public static void setArrowsPerEnd(int arrowsPerPasse) {
        lastUsed.edit()
                .putInt(KEY_ARROWS_PER_PASSE, arrowsPerPasse)
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
                .putInt(KEY_TARGET, (int) target.getId())
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

    public static int getPasses() {
        return lastUsed.getInt(KEY_PASSES, 10);
    }

    public static void setPasses(int passes) {
        lastUsed.edit()
                .putInt(KEY_PASSES, passes)
                .apply();
    }

    public static boolean getTranslationDialogWasShown() {
        SharedPreferences prefs = preferences;
        return prefs.getBoolean(KEY_TRANSLATION_DIALOG_SHOWN, false);
    }

    public static void setTranslationDialogWasShown(boolean shown) {
        preferences
                .edit()
                .putBoolean(KEY_TRANSLATION_DIALOG_SHOWN, shown)
                .apply();
    }

    public static boolean getInputMode() {
        return preferences
                .getBoolean(KEY_INPUT_MODE, false);
    }

    public static void setInputMode(boolean inputMode) {
        preferences
                .edit()
                .putBoolean(KEY_INPUT_MODE, inputMode)
                .apply();
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

    public static boolean getTimerVibrate() {
        return preferences
                .getBoolean(KEY_TIMER_VIBRATE, false);
    }

    public static boolean getTimerSoundEnabled() {
        return preferences
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
        SharedPreferences prefs = preferences;
        try {
            return Integer.parseInt(prefs.getString(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getProfileFirstName() {
        return preferences
                .getString(KEY_PROFILE_FIRST_NAME, "");
    }

    public static String getProfileLastName() {
        return preferences
                .getString(KEY_PROFILE_LAST_NAME, "");
    }

    public static String getProfileFullName() {
        return String.format("%s %s", getProfileFirstName(), getProfileLastName());
    }

    public static String getProfileClub() {
        return preferences
                .getString(KEY_PROFILE_CLUB, "");
    }

    private static LocalDate getProfileBirthDay() {
        String date = preferences
                .getString(KEY_PROFILE_BIRTHDAY, "");
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date);
    }

    public static void setProfileBirthDay(LocalDate birthDay) {
        preferences
                .edit()
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

    public static int getBackupInterval() {
        return Integer.parseInt(preferences.getString(KEY_BACKUP_INTERVAL, "7"));
    }

    public static void setBackupInterval(int interval) {
        preferences.edit()
                .putString(KEY_BACKUP_INTERVAL, String.valueOf(interval))
                .apply();
    }

    public static String getBackupIntervalString() {
        switch (getBackupInterval()) {
            case 1:
                return get(R.string.daily);
            case 7:
                return get(R.string.weekly);
            default:
                return get(R.string.monthly);
        }
    }
}
