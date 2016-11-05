package de.dreier.mytargets.features.settings;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;

import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.features.settings.backup.BackupSettingsFragment;
import de.dreier.mytargets.fragments.AboutFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static de.dreier.mytargets.features.settings.MainSettingsFragmentPermissionsDispatcher.*;

@RuntimePermissions
public class MainSettingsFragment extends SettingsFragmentBase {

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case "pref_backup":
                doBackupWithCheck(this);
                return true;
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

    //TODO move into backup activity
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void doBackup() {
        startActivity(new Intent(getContext(), BackupSettingsFragment.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainSettingsFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
