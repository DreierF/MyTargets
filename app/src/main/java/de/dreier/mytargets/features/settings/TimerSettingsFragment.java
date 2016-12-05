package de.dreier.mytargets.features.settings;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;

import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_SHOOT_TIME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_WAIT_TIME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_WARN_TIME;

public class TimerSettingsFragment extends SettingsFragmentBase {
    @Override
    protected void updateItemSummaries() {
        setSecondsSummary(KEY_TIMER_WAIT_TIME, SettingsManager.getTimerWaitTime());
        setSecondsSummary(KEY_TIMER_SHOOT_TIME, SettingsManager.getTimerShootTime());
        setSecondsSummary(KEY_TIMER_WARN_TIME, SettingsManager.getTimerWarnTime());
    }

    private void setSecondsSummary(String key, int value) {
        setSummary(key, getResources().getQuantityString(R.plurals.second, value, value));
    }
}
