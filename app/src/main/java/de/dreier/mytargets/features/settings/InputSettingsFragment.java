package de.dreier.mytargets.features.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.views.DatePreference;
import de.dreier.mytargets.views.DatePreferenceDialogFragmentCompat;

import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_ARROW_DIAMETER_SCALE;
import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_TARGET_ZOOM;

public class InputSettingsFragment extends SettingsFragmentBase {
    @Override
    protected void updateItemSummaries() {
        setSummary(KEY_INPUT_ARROW_DIAMETER_SCALE,
                SettingsManager.getInputArrowDiameterScale() + "x");
        setSummary(KEY_INPUT_TARGET_ZOOM, SettingsManager.getInputTargetZoom() + "x");
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!(preference instanceof DatePreference)) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        DialogFragment dialogFragment = new DatePreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(this.getFragmentManager(),
                "android.support.v7.preference.PreferenceFragment.DIALOG");
    }
}
