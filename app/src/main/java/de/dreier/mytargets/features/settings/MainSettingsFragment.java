package de.dreier.mytargets.features.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;

import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.fragments.AboutFragment;
import permissions.dispatcher.RuntimePermissions;

import static de.dreier.mytargets.features.settings.MainSettingsFragmentPermissionsDispatcher.doBackupWithCheck;

@RuntimePermissions
public class MainSettingsFragment extends SettingsFragmentBase {

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case "pref_about":
                AboutFragment.getIntent().withContext(this).start();
                return true;
            case "pref_licence":
                startActivity(new Intent(getContext(),
                        SimpleFragmentActivityBase.LicencesActivity.class));
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainSettingsFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }
}