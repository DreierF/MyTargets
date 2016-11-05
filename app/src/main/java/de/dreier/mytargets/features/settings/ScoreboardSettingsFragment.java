package de.dreier.mytargets.features.settings;

import de.dreier.mytargets.managers.SettingsManager;

import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_BIRTHDAY;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_CLUB;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_FIRST_NAME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_LAST_NAME;

public class ScoreboardSettingsFragment extends SettingsFragmentBase {

    @Override
    public void updateItemSummaries() {
        setSummary(KEY_PROFILE_FIRST_NAME, SettingsManager.getProfileFirstName());
        setSummary(KEY_PROFILE_LAST_NAME, SettingsManager.getProfileLastName());
        setSummary(KEY_PROFILE_BIRTHDAY, SettingsManager.getProfileBirthDayFormatted());
        setSummary(KEY_PROFILE_CLUB, SettingsManager.getProfileClub());
    }
}
