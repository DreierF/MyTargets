package de.dreier.mytargets.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.backup.BackupActivity;
import de.dreier.mytargets.views.DatePreference;
import de.dreier.mytargets.views.DatePreferenceDialogFragmentCompat;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static de.dreier.mytargets.fragments.SettingsFragmentPermissionsDispatcher.doBackupWithCheck;
import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_ARROW_DIAMETER_SCALE;
import static de.dreier.mytargets.managers.SettingsManager.KEY_INPUT_TARGET_ZOOM;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_BIRTHDAY;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_CLUB;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_FIRST_NAME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_PROFILE_LAST_NAME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_SHOOT_TIME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_WAIT_TIME;
import static de.dreier.mytargets.managers.SettingsManager.KEY_TIMER_WARN_TIME;

@RuntimePermissions
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if (rootKey == null) {
            getActivity().setTitle(R.string.preferences);
        } else if ("input".equals(rootKey)) {
            getActivity().setTitle(R.string.input);
            updateInputSummaries();
        } else if ("timer".equals(rootKey)) {
            getActivity().setTitle(R.string.timer);
            updateTimerSummaries();
        } else if ("scoreboard".equals(rootKey)) {
            getActivity().setTitle(R.string.scoreboard);
            updateProfileSummaries();
        }
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

    @SuppressLint("PrivateResource")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.background_material_light));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case "pref_backup":
                doBackupWithCheck(this);
                return true;
            case "pref_about":
                startActivity(new Intent(getContext(), SimpleFragmentActivityBase.AboutActivity.class));
                return true;
            case "pref_licence":
                startActivity(new Intent(getContext(), SimpleFragmentActivityBase.LicencesActivity.class));
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SettingsFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //TODO move into backup activity
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void doBackup() {
        startActivity(new Intent(getContext(), BackupActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith("timer_")) {
            updateTimerSummaries();
        } else if (key.startsWith("profile_")) {
            updateProfileSummaries();
        } else if (key.startsWith("input_")) {
            updateInputSummaries();
        }
    }

    private void updateProfileSummaries() {
        setSummary(KEY_PROFILE_FIRST_NAME, SettingsManager.getProfileFirstName());
        setSummary(KEY_PROFILE_LAST_NAME, SettingsManager.getProfileLastName());
        setSummary(KEY_PROFILE_BIRTHDAY, SettingsManager.getProfileBirthDayFormatted());
        setSummary(KEY_PROFILE_CLUB, SettingsManager.getProfileClub());
    }

    private void updateTimerSummaries() {
        setSecondsSummary(KEY_TIMER_WAIT_TIME, SettingsManager.getTimerWaitTime());
        setSecondsSummary(KEY_TIMER_SHOOT_TIME, SettingsManager.getTimerShootTime());
        setSecondsSummary(KEY_TIMER_WARN_TIME, SettingsManager.getTimerWarnTime());
    }

    private void updateInputSummaries() {
        setSummary(KEY_INPUT_ARROW_DIAMETER_SCALE, SettingsManager.getInputArrowDiameterScale() + "x");
        setSummary(KEY_INPUT_TARGET_ZOOM, SettingsManager.getInputTargetZoom() + "x");
    }

    private void setSecondsSummary(String key, int value) {
        setSummary(key, getResources().getQuantityString(R.plurals.second, value, value));
    }

    private void setSummary(String key, String value) {
        findPreference(key).setSummary(value);
    }
}
