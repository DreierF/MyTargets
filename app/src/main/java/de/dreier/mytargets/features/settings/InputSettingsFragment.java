package de.dreier.mytargets.features.settings;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;

import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_ARROW_DIAMETER_SCALE;
import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_KEYBOARD_TYPE;
import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_TARGET_ZOOM;
import static de.dreier.mytargets.views.TargetView.EKeyboardType.LEFT;

public class InputSettingsFragment extends SettingsFragmentBase {

    @Override
    protected void updateItemSummaries() {
        setSummary(KEY_INPUT_ARROW_DIAMETER_SCALE,
                SettingsManager.getInputArrowDiameterScale() + "x");
        setSummary(KEY_INPUT_TARGET_ZOOM, SettingsManager.getInputTargetZoom() + "x");
        setSummary(KEY_INPUT_KEYBOARD_TYPE, SettingsManager.getInputKeyboardType() == LEFT
                ? getString(R.string.left_handed) : getString(R.string.right_handed));
    }
}
