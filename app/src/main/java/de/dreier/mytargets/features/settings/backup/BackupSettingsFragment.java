/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings.backup;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentBackupBinding;
import de.dreier.mytargets.features.settings.SettingsFragmentBase;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.settings.backup.provider.BackupUtils;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.settings.backup.provider.IAsyncBackupRestore;
import de.dreier.mytargets.features.settings.backup.synchronization.GenericAccountService;
import de.dreier.mytargets.features.settings.backup.synchronization.SyncUtils;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
import static android.content.ContentResolver.SYNC_OBSERVER_TYPE_PENDING;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.features.settings.backup.BackupSettingsFragmentPermissionsDispatcher.showFilePickerWithPermissionCheck;
import static de.dreier.mytargets.features.settings.backup.provider.GoogleDriveBackup.AsyncRestore.REQUEST_CODE_RESOLUTION;

@RuntimePermissions
public class BackupSettingsFragment extends SettingsFragmentBase implements IAsyncBackupRestore.OnLoadFinishedListener {

    private static final int IMPORT_FROM_URI = 1234;

    private IAsyncBackupRestore backup;
    @Nullable
    private BackupAdapter adapter;
    private FragmentBackupBinding binding;
    private Timer updateLabelTimer;
    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     * <p>
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    @Nullable
    private Object syncObserverHandle;
    private boolean isRefreshing = false;

    /**
     * Create a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Progress
     * bar. If a sync is active or pending, the progress is shown.
     */
    @NonNull
    private SyncStatusObserver syncStatusObserver = which -> getActivity().runOnUiThread(() -> {
        Account account = GenericAccountService.getAccount();

        boolean syncActive = ContentResolver.isSyncActive(account, SyncUtils.CONTENT_AUTHORITY);
        boolean syncPending = ContentResolver.isSyncPending(account, SyncUtils.CONTENT_AUTHORITY);
        boolean wasRefreshing = isRefreshing;
        isRefreshing = syncActive || syncPending;
        binding.backupNowButton.setEnabled(!isRefreshing);
        binding.backupProgressBar.setIndeterminate(true);
        binding.backupProgressBar.setVisibility(isRefreshing ? VISIBLE : GONE);
        if (wasRefreshing && !isRefreshing && backup != null) {
            backup.getBackups(this);
        }
    });

    /**
     * Create SyncAccount at launch, if needed.
     * <p>
     * <p>This will create a new account with the system for our application and register our
     * {@link de.dreier.mytargets.features.settings.backup.synchronization.SyncService} with it.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Create account, if needed
        SyncUtils.createSyncAccount(context);
    }

    @Override
    public void onCreatePreferences() {
        /* Overridden to no do anything. Normally this would try to inflate the preferences,
        * but in this case we want to show our own UI. */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup, container, false);
        ToolbarUtils.showHomeAsUp(this);

        binding.backupNowButton.setOnClickListener(v -> SyncUtils.triggerBackup());

        binding.automaticBackupSwitch.setOnClickListener(v -> onAutomaticBackupChanged());

        binding.backupIntervalPreference.getRoot()
                .setOnClickListener(view -> onBackupIntervalClicked());
        binding.backupIntervalPreference.image
                .setImageResource(R.drawable.ic_query_builder_grey600_24dp);
        binding.backupIntervalPreference.name.setText(R.string.backup_interval);
        updateInterval();

        binding.backupLocation.getRoot().setOnClickListener(v -> onBackupLocationClicked());

        binding.recentBackupsList.setNestedScrollingEnabled(false);
        binding.recentBackupsList
                .addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.backup_import, menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_import) {
            showFilePickerWithPermissionCheck(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        applyBackupLocationWithPermissionCheck(SettingsManager.getBackupLocation());
        updateAutomaticBackupSwitch();

        syncStatusObserver.onStatusChanged(0);
        syncObserverHandle = ContentResolver.addStatusChangeListener(
                SYNC_OBSERVER_TYPE_PENDING | SYNC_OBSERVER_TYPE_ACTIVE, syncStatusObserver);
    }

    @Override
    public void onPause() {
        if (backup != null) {
            backup.stop();
        }
        if (updateLabelTimer != null) {
            updateLabelTimer.cancel();
        }
        if (syncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(syncObserverHandle);
            syncObserverHandle = null;
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @NonNull final Intent data) {
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode != RESULT_OK) {
            leaveBackupSettings();
        }
        if (requestCode == IMPORT_FROM_URI && resultCode == AppCompatActivity.RESULT_OK) {
            importFromUri(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAutomaticBackupChanged() {
        boolean autoBackupEnabled = binding.automaticBackupSwitch.isChecked();
        binding.backupIntervalLayout.setVisibility(autoBackupEnabled ? VISIBLE : GONE);
        SyncUtils.setSyncAutomaticallyEnabled(autoBackupEnabled);
    }

    private void updateAutomaticBackupSwitch() {
        boolean autoBackupEnabled = SyncUtils.isSyncAutomaticallyEnabled();
        binding.automaticBackupSwitch.setChecked(autoBackupEnabled);
    }

    private void onBackupIntervalClicked() {
        final List<EBackupInterval> backupIntervals = Arrays.asList(EBackupInterval.values());
        new MaterialDialog.Builder(getContext())
                .title(R.string.backup_interval)
                .items(backupIntervals)
                .itemsCallbackSingleChoice(
                        backupIntervals.indexOf(SettingsManager.getBackupInterval()),
                        (dialog, v, index, text) -> {
                            SettingsManager.setBackupInterval(EBackupInterval.values()[index]);
                            updateInterval();
                            return true;
                        })
                .show();
    }

    private void updateInterval() {
        boolean autoBackupEnabled = SyncUtils.isSyncAutomaticallyEnabled();
        binding.backupIntervalLayout.setVisibility(autoBackupEnabled ? VISIBLE : GONE);
        binding.backupIntervalPreference.summary
                .setText(SettingsManager.getBackupInterval().toString());
    }

    private void onBackupLocationClicked() {
        EBackupLocation item = SettingsManager.getBackupLocation();
        new MaterialDialog.Builder(getContext())
                .title(R.string.backup_location)
                .items(EBackupLocation.getList())
                .itemsCallbackSingleChoice(EBackupLocation.getList().indexOf(item),
                        (dialog, itemView, index, text) -> {
                            EBackupLocation location = EBackupLocation.getList().get(index);
                            if (backup != null) {
                                backup.stop();
                            }
                            applyBackupLocationWithPermissionCheck(location);
                            return true;
                        })
                .show();
    }

    private void updateBackupLocation() {
        EBackupLocation backupLocation = SettingsManager.getBackupLocation();
        binding.backupLocation.image.setImageResource(backupLocation.getDrawableRes());
        binding.backupLocation.name.setText(R.string.backup_location);
        binding.backupLocation.summary.setText(backupLocation.toString());
    }

    private void applyBackupLocationWithPermissionCheck(@NonNull EBackupLocation item) {
        if (item.needsStoragePermissions()) {
            BackupSettingsFragmentPermissionsDispatcher
                    .applyBackupLocationWithPermissionCheck(this, item);
        } else {
            applyBackupLocation(item);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void applyBackupLocation(@NonNull EBackupLocation item) {
        SettingsManager.setBackupLocation(item);
        backup = item.createAsyncRestore();
        binding.recentBackupsProgress.setVisibility(VISIBLE);
        binding.recentBackupsList.setVisibility(GONE);
        adapter = new BackupAdapter(getContext(), this::showBackupDetails, this::deleteBackup);
        binding.recentBackupsList.setAdapter(adapter);
        backup.connect(getActivity(), new IAsyncBackupRestore.ConnectionListener() {
            @Override
            public void onConnected() {
                updateBackupLocation();
                backup.getBackups(BackupSettingsFragment.this);
            }

            @Override
            public void onConnectionSuspended() {
                showError(R.string.loading_backups_failed, getString(R.string.connection_failed));
            }
        });
    }

    @Override
    protected void setActivityTitle() {
        getActivity().setTitle(R.string.backup_action);
    }

    private void onBackupsLoaded(@NonNull List<BackupEntry> list) {
        binding.recentBackupsProgress.setVisibility(GONE);
        binding.recentBackupsList.setVisibility(VISIBLE);
        adapter.setList(list);
        binding.lastBackupLabel.setVisibility(list.size() > 0 ? VISIBLE : GONE);
        if (updateLabelTimer != null) {
            updateLabelTimer.cancel();
        }
        if (list.size() > 0) {
            final long time = list.get(0).getModifiedDate().getTime();
            updateLabelTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(() ->
                            binding.lastBackupLabel
                                    .setText(getString(R.string.last_backup, DateUtils
                                            .getRelativeTimeSpanString(time))));
                }
            };
            updateLabelTimer.schedule(timerTask, 0, 10000);
        }
    }

    private void showError(@StringRes int title, @NonNull String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(message)
                .positiveText(android.R.string.ok)
                .show();
    }

    private MaterialDialog showRestoreProgressDialog() {
        return new MaterialDialog.Builder(getContext())
                .content(R.string.restoring)
                .progress(true, 0)
                .show();
    }

    private void showBackupDetails(@NonNull BackupEntry item) {
        final String html = String.format(Locale.US,
                "%s<br><br><b>%s</b><br>%s<br>%s",
                getString(R.string.restore_desc),
                getString(R.string.backup_details),
                SimpleDateFormat.getDateTimeInstance()
                        .format(item.getModifiedDate()),
                item.getHumanReadableSize()
        );
        new MaterialDialog.Builder(getContext())
                .title(R.string.dialog_restore_title)
                .content(Utils.fromHtml(html))
                .positiveText(R.string.restore)
                .negativeText(android.R.string.cancel)
                .positiveColor(0xffe53935)
                .negativeColor(0x88000000)
                .onPositive((dialog, which) -> restoreBackup(item))
                .show();
    }

    private void restoreBackup(BackupEntry item) {
        MaterialDialog progress = showRestoreProgressDialog();
        backup.restoreBackup(item,
                new IAsyncBackupRestore.BackupStatusListener() {
                    @Override
                    public void onFinished() {
                        progress.dismiss();
                        Utils.doRestart(getContext());
                    }

                    @Override
                    public void onError(@NonNull String message) {
                        progress.dismiss();
                        showError(R.string.restore_failed, message);
                    }
                });
    }

    private void deleteBackup(BackupEntry backupEntry) {
        backup.deleteBackup(backupEntry, new IAsyncBackupRestore.BackupStatusListener() {
            @Override
            public void onFinished() {
                adapter.remove(backupEntry);
                backup.getBackups(BackupSettingsFragment.this);
            }

            @Override
            public void onError(@NonNull String message) {
                showError(R.string.delete_failed, message);
            }
        });
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showFilePicker() {
        final Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("application/zip");
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.select_a_file));
        startActivityForResult(intent, IMPORT_FROM_URI);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForWrite() {
        leaveBackupSettings();
    }

    private void leaveBackupSettings() {
        Handler h = new Handler();
        h.post(() -> getActivity().getSupportFragmentManager().popBackStack());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BackupSettingsFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void importFromUri(@NonNull final Uri uri) {
        MaterialDialog progress = showRestoreProgressDialog();
        new AsyncTask<Void, Void, String>() {
            @Nullable
            @Override
            protected String doInBackground(Void... params) {
                try {
                    InputStream st = getContext().getContentResolver().openInputStream(uri);
                    BackupUtils.importZip(getContext(), st);
                    return null;
                } catch (FileNotFoundException ioe) {
                    ioe.printStackTrace();
                    return getString(R.string.file_not_found);
                } catch (Exception e) {
                    e.printStackTrace();
                    return getString(R.string.failed_reading_file);
                }
            }

            @Override
            protected void onPostExecute(@Nullable String errorMessage) {
                progress.dismiss();
                if (errorMessage == null) {
                    Utils.doRestart(getContext());
                } else {
                    showError(R.string.import_failed, errorMessage);
                }
            }
        }.execute();
    }

    @Override
    public void onLoadFinished(@NonNull List<BackupEntry> backupEntries) {
        onBackupsLoaded(backupEntries);
    }

    @Override
    public void onError(@NonNull String message) {
        showError(R.string.loading_backups_failed, message);
    }
}
