package de.dreier.mytargets.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.MainActivity;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.IABHelperWrapper;
import de.dreier.mytargets.utils.Utils;
import de.psdev.licensesdialog.LicensesDialog;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        DonateDialogFragment.DonationListener {
    private static final int REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_WRITE_STORAGE_EXPORT = 2;
    private static final int REQUEST_WRITE_STORAGE_BACKUP = 3;
    private IABHelperWrapper mIABWrapper;


    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if (rootKey == null) {
            initRoot();
            getActivity().setTitle(R.string.preferences);
        } else if (rootKey.equals("timer")) {
            getActivity().setTitle(R.string.timer);
        } else if (rootKey.equals("scoreboard")) {
            getActivity().setTitle(R.string.scoreboard);
        }
    }

    private void initRoot() {
        Preference version = getPreferenceScreen().findPreference("pref_version");
        PackageInfo appVersionInfo = Utils.getAppVersionInfo(getContext());
        if (appVersionInfo != null) {
            String versionName = appVersionInfo.versionName;
            version.setSummary(getString(R.string.version, versionName));
        } else {
            version.setSummary(getString(R.string.version, "unknown"));
        }

        setSecondsSummary("timer_wait_time", "10");
        setSecondsSummary("timer_shoot_time", "120");
        setSecondsSummary("timer_warn_time", "30");
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_material_light));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIABWrapper = new IABHelperWrapper((AppCompatActivity) getActivity());
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert ab != null;
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();
        mIABWrapper.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case "pref_import":
                doImport();
                return true;
            case "pref_backup":
                doBackup();
                return true;
            case "pref_export":
                doExport();
                return true;
            case "pref_rate":
                rate();
                return true;
            case "pref_share":
                share();
                return true;
            case "pref_contact":
                contact();
                return true;
            case "pref_donate":
                mIABWrapper.showDialog(this);
                return true;
            case "pref_licence":
                new LicensesDialog.Builder(getActivity())
                        .setTitle(R.string.licences)
                        .setNotices(R.raw.licences)
                        .setIncludeOwnLicense(false).build().show();
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    private void contact() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:dreier.florian@gmail.com"));
        startActivity(intent);
    }

    private void share() {
        final String appPackageName = getActivity().getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "http://play.google.com/store/apps/details?id=" +
                        appPackageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void save(final boolean export) {
        new AsyncTask<Void, Void, Uri>() {

            @Override
            protected Uri doInBackground(Void... params) {
                try {
                    if (export) {
                        return BackupUtils.export(getActivity().getApplicationContext());
                    } else {
                        return BackupUtils.backup(getActivity().getApplicationContext());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Uri uri) {
                super.onPostExecute(uri);
                if (uri != null) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                    if (export) {
                        email.setType("text/csv");
                    } else {
                        email.setType("application/zip");
                    }
                    startActivity(
                            Intent.createChooser(email, getString(R.string.send_exported)));
                } else {
                    Toast.makeText(getActivity(),
                            export ? R.string.exporting_failed : R.string.backup_failed,
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void rate() {
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void doImport() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showFilePicker();
            return;
        }

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_STORAGE);
    }

    private void doBackup() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            save(false);
            return;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE_BACKUP);
    }

    private void doExport() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            save(true);
            return;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE_EXPORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFilePicker();
                } else {
                    Log.e("Permission", "Denied");
                }
                break;
            case REQUEST_WRITE_STORAGE_BACKUP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save(false);
                } else {
                    Log.e("Permission", "Denied");
                }
                break;
            case REQUEST_WRITE_STORAGE_EXPORT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save(true);
                } else {
                    Log.e("Permission", "Denied");
                }
                break;

        }
    }

    private void showFilePicker() {
        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.select_a_file));
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIABWrapper.handleActivityResult(requestCode, resultCode, data)) {
            if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
                final Uri uri = data.getData();
                if (BackupUtils.Import(getActivity(), uri)) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith("timer_")) {
            setSecondsSummary("timer_wait_time", "10");
            setSecondsSummary("timer_shoot_time", "120");
            setSecondsSummary("timer_warn_time", "30");
        }
    }

    private void setSecondsSummary(String key, String def) {
        Preference pref = findPreference(key);
        int sec;
        try {
            sec = Integer.parseInt(
                    getPreferenceManager().getSharedPreferences().getString(key, def));
        } catch (NumberFormatException e) {
            sec = Integer.parseInt(def);
            getPreferenceManager().getSharedPreferences().edit().putString(key, def).apply();
        }
        pref.setSummary(getResources().getQuantityString(R.plurals.second, sec, sec));
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
    }

    @Override
    public void onDonate(int position) {
        mIABWrapper.startDonationForItem(position);
    }
}