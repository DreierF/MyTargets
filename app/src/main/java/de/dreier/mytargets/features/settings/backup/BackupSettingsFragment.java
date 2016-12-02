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
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.BackupAdapter;
import de.dreier.mytargets.databinding.FragmentBackupBinding;
import de.dreier.mytargets.features.settings.SettingsFragmentBase;
import de.dreier.mytargets.features.settings.backup.synchronization.SyncUtils;
import de.dreier.mytargets.features.settings.backup.synchronization.GenericAccountService;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.features.settings.backup.BackupSettingsFragmentPermissionsDispatcher.applyBackupLocationWithCheck;
import static de.dreier.mytargets.features.settings.backup.BackupSettingsFragmentPermissionsDispatcher.showFilePickerWithCheck;

@RuntimePermissions
public class BackupSettingsFragment extends SettingsFragmentBase {

    private static final int IMPORT_FROM_URI = 1234;

    private IBackup backup;
    private BackupAdapter adapter;
    private FragmentBackupBinding binding;


    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     * <p>
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;
    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = which -> getActivity().runOnUiThread(() -> {
        // Create a handle to the account that was created by
        // SyncService.createSyncAccount(). This will be used to query the system to
        // see how the sync status has changed.
        Account account = GenericAccountService.getAccount();
        if (account == null) {
            // getAccount() returned an invalid value. This shouldn't happen, but
            // we'll set the status to "not refreshing".
            binding.backupNowButton.setEnabled(false);
            binding.backupProgressBar.setVisibility(GONE);
            return;
        }

        // Test the ContentResolver to see if the sync adapter is active or pending.
        // Set the state of the refresh button accordingly.
        boolean syncActive = ContentResolver.isSyncActive(
                account, getContext().getPackageName());
        boolean syncPending = ContentResolver.isSyncPending(
                account, getContext().getPackageName());
        boolean refreshing = syncActive || syncPending;
        binding.backupNowButton.setEnabled(!refreshing);
        binding.backupProgressBar.setIndeterminate(true);
        binding.backupProgressBar.setVisibility(refreshing ? VISIBLE : GONE);
    });

    @Override
    public void onCreatePreferences() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup, container, false);
        ToolbarUtils.showHomeAsUp(this);

        binding.recentBackupsList.setNestedScrollingEnabled(false);
        binding.backupLocation.setOnActivityResultContext(this);
        binding.backupLocation.setOnUpdateListener(item -> {
            SettingsManager.setBackupLocation(item);
            if (backup != null) {
                backup.stop();
            }
            setBackupLocation(item);
        });

        binding.backupNowButton.setOnClickListener(v -> backupNow());
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    /**
     * Create SyncAccount at launch, if needed.
     * <p>
     * <p>This will create a new account with the system for our application, register our
     * {@link de.dreier.mytargets.features.settings.backup.synchronization.SyncService} with it, and establish a sync schedule.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Create account, if needed
        SyncUtils.createSyncAccount(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        EBackupLocation backupLocation = SettingsManager.getBackupLocation();
        binding.backupLocation.setItem(backupLocation);

        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    protected void updateItemSummaries() {
//        setSummary(SettingsManager.KEY_BACKUP_INTERVAL, SettingsManager.getBackupIntervalString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.backup_import, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_import) {
            showFilePickerWithCheck(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (backup != null) {
            backup.stop();
        }
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        backup.onActivityResult(requestCode, resultCode, data);
        binding.backupLocation.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_FROM_URI && resultCode == AppCompatActivity.RESULT_OK) {
            importFromUri(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setBackupLocation(EBackupLocation item) {
        if (item.needsStoragePermissions()) {
            applyBackupLocationWithCheck(this, item);
        } else {
            applyBackupLocation(item);
        }
    }

    @Override
    protected void setActivityTitle() {
        getActivity().setTitle(R.string.backup_action);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void applyBackupLocation(EBackupLocation item) {
        backup = item.createBackup();
        adapter = new BackupAdapter(getContext(), this::showBackupDetails, this::deleteBackup);
        binding.recentBackupsList.setAdapter(adapter);
        backup.start(getActivity(), new IBackup.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(List<BackupEntry> backupEntries) {
                onBackupsLoaded(backupEntries);
            }

            @Override
            public void onError(String message) {
                showError(R.string.loading_backups_failed, message);
            }
        });
    }

    private void onBackupsLoaded(List<BackupEntry> list) {
        adapter.setList(list);
        binding.lastBackupLabel.setVisibility(list.size() > 0 ? VISIBLE : GONE);
        if (list.size() > 0) {
            binding.lastBackupLabel.setText(getString(R.string.last_backup, DateUtils
                    .getRelativeTimeSpanString(list.get(0).getModifiedDate().getTime())));
        }
    }

    private void backupNow() {
        SyncUtils.triggerBackup();
    }

    private void showError(@StringRes int title, String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(message)
                .positiveText(android.R.string.ok)
                .show();
    }

    private MaterialDialog showProgressDialog(@StringRes int title) {
        return new MaterialDialog.Builder(getContext())
                .content(title)
                .progress(true, 0)
                .show();
    }

    private void showBackupDetails(BackupEntry item) {
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
                .content(HtmlUtils.fromHtml(html))
                .positiveText(R.string.restore)
                .negativeText(android.R.string.cancel)
                .positiveColor(0xffe53935)
                .negativeColor(0x88000000)
                .onPositive((dialog, which) -> restoreBackup(item))
                .show();
    }

    private void restoreBackup(BackupEntry item) {
        MaterialDialog progress = showProgressDialog(R.string.restoring);
        backup.restoreBackup(item,
                new IBackup.BackupStatusListener() {
                    @Override
                    public void onFinished() {
                        progress.dismiss();
                        Utils.doRestart(getContext());
                    }

                    @Override
                    public void onError(String message) {
                        progress.dismiss();
                        showError(R.string.restore_failed, message);
                    }
                });
    }

    private void deleteBackup(BackupEntry backupEntry) {
        backup.deleteBackup(backupEntry, new IBackup.BackupStatusListener() {
            @Override
            public void onFinished() {
                adapter.remove(backupEntry);
                backup.getBackups();
            }

            @Override
            public void onError(String message) {
                showError(R.string.delete_failed, message);
            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showFilePicker() {
        final Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("*/zip");
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.select_a_file));
        startActivityForResult(intent, IMPORT_FROM_URI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BackupSettingsFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void importFromUri(final Uri uri) {
        MaterialDialog progress = showProgressDialog(R.string.restoring);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    InputStream st = getContext().getContentResolver().openInputStream(uri);
                    DatabaseManager.Import(getContext(), st);
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
            protected void onPostExecute(String errorMessage) {
                progress.dismiss();
                if (errorMessage == null) {
                    Utils.doRestart(getContext());
                } else {
                    showError(R.string.import_failed, errorMessage);
                }
            }
        }.execute();
    }
}