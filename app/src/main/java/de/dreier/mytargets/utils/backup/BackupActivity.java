package de.dreier.mytargets.utils.backup;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.BackupAdapter;
import de.dreier.mytargets.databinding.BackupDriveActivityBinding;
import de.dreier.mytargets.managers.SettingsManager;

public class BackupActivity extends AppCompatActivity {

    private Backup backup;
    private BackupAdapter adapter;
    private Backup.BackupStatusListener backupStatusListener = new Backup.BackupStatusListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onFinished() {
            backup.getBackups();
        }

        @Override
        public void onError(String message) {
            new MaterialDialog.Builder(BackupActivity.this)
                    .title(R.string.backup_failed)
                    .content(message)
                    .positiveText(android.R.string.ok)
                    .show();
        }
    };
    private BackupDriveActivityBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.backup_drive_activity);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EBackupLocation backupLocation = SettingsManager.getBackupLocation();
        setBackupLocation(backupLocation);
        binding.backupLocation.setOnActivityResultContext(this);
        binding.backupLocation.setItem(backupLocation);
        binding.backupLocation.setOnUpdateListener(item -> {
            if (item == SettingsManager.getBackupLocation()) {
                return;
            }
            SettingsManager.setBackupLocation(item);
            backup.stop();
            setBackupLocation(item);
        });

        binding.backupNowButton.setOnClickListener(v -> backup.startBackup(backupStatusListener));
    }

    private void setBackupLocation(EBackupLocation item) {
        backup = item.createBackup();
        adapter = new BackupAdapter(this, this::onRestoreBackup, null);
        binding.recentBackupsList.setAdapter(adapter);
        backup.start(this, adapter::setList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backup.start(this, adapter::setList);
    }

    @Override
    protected void onPause() {
        backup.stop();
        super.onPause();
    }

    private void onRestoreBackup(BackupEntry item) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_restore_backup);
        TextView createdTextView = (TextView) dialog
                .findViewById(R.id.dialog_backup_restore_created);
        TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
        Button restoreButton = (Button) dialog
                .findViewById(R.id.dialog_backup_restore_button_restore);
        Button cancelButton = (Button) dialog
                .findViewById(R.id.dialog_backup_restore_button_cancel);

        createdTextView.setText(SimpleDateFormat.getDateTimeInstance()
                .format(item.getModifiedDate()));
        sizeTextView.setText(item.getHumanReadableSize());

        restoreButton.setOnClickListener(v -> backup.restoreBackup(item, backupStatusListener));
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        backup.onActivityResult(requestCode, resultCode, data);
        binding.backupLocation.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}