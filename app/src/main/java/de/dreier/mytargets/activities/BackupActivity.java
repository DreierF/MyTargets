package de.dreier.mytargets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.BackupAdapter;
import de.dreier.mytargets.databinding.BackupDriveActivityBinding;
import de.dreier.mytargets.utils.Backup;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.Utils;

public class BackupActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_PICKER = 2;
    private static final int REQUEST_CODE_PICKER_FOLDER = 4;
    private static final String TAG = "glucosio_drive_backup";
    private static final String BACKUP_FOLDER_KEY = "backup_folder";
    private GoogleApiClient googleApiClient;
    private IntentSender intentPicker;
    private String backupFolder;
    private SharedPreferences sharedPref;
    private BackupAdapter adapter;
    private BackupDriveActivityBinding binding;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.backup_drive_activity);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.backup_action));

        adapter = new BackupAdapter(this);
        binding.activityBackupDriveListviewRestore.setAdapter(adapter);

        binding.activityBackupDriveButtonBackup.setOnClickListener(v -> {
            // Open Folder picker, then upload the file on Drive
            openFolderPicker(true);
        });

        binding.activityBackupDriveButtonFolder.setOnClickListener(v -> {
            // Check first if a folder is already selected
            if (!"".equals(backupFolder)) {
                //Start the picker to choose a folder
                //False because we don't want to upload the backup on drive then
                openFolderPicker(false);
            }
        });

        binding.activityBackupDriveButtonManageDrive
                .setOnClickListener(v -> openOnDrive(DriveId.decodeFromString(backupFolder)));

        // Show backup folder, if exists
        backupFolder = sharedPref.getString(BACKUP_FOLDER_KEY, "");
        if (!("").equals(backupFolder)) {
            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            binding.activityBackupDriveButtonManageDrive.setVisibility(View.VISIBLE);
        }

        // Populate backup list
        if (!("").equals(backupFolder)) {
            getBackupsFromDrive(DriveId.decodeFromString(backupFolder).asDriveFolder());
        }
    }

    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((googleApiClient)).setResultCallback(
                result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    binding.activityBackupDriveTextviewFolder.setText(metadata.getTitle());
                }
        );
    }

    private void openFolderPicker(boolean uploadToDrive) {
        if (uploadToDrive) {
            // First we check if a backup folder is set
            if (TextUtils.isEmpty(backupFolder)) {
                try {
                    if (googleApiClient != null && googleApiClient.isConnected()) {
                        if (intentPicker == null) {
                            intentPicker = buildIntent();
                        }
                        //Start the picker to choose a folder
                        startIntentSenderForResult(
                                intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
                    }
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Unable to send intent", e);
                    showErrorDialog();
                }
            } else {
                uploadToDrive(DriveId.decodeFromString(backupFolder));
            }
        } else {
            try {
                intentPicker = null;
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    if (intentPicker == null) {
                        intentPicker = buildIntent();
                    }
                    //Start the picker to choose a folder
                    startIntentSenderForResult(
                            intentPicker, REQUEST_CODE_PICKER_FOLDER, null, 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
                showErrorDialog();
            }
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(googleApiClient);
    }

    private void getBackupsFromDrive(DriveFolder folder) {
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "backup.zip"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        folder.queryChildren(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<Backup> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i = 0; i < size; i++) {
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            DateTime modifiedDate = new DateTime(metadata.getModifiedDate());
                            long backupSize = metadata.getFileSize();
                            backupsArray.add(new Backup(driveId, modifiedDate, backupSize));
                            adapter.setList(backupsArray);
                        }

                    }
                });
    }

    public void downloadFromDrive(DriveFile file) {
        file.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }

                    // DriveContents object contains pointers to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    InputStream input = contents.getInputStream();

                    try {
                        BackupUtils.unzip(BackupActivity.this, input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        safeCloseClosable(input);
                    }

                    /*Toast.makeText(getApplicationContext(),
                            R.string.activity_backup_drive_message_restart, Toast.LENGTH_LONG)
                            .show();*/

                    Utils.doRestart(this);
                });
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(googleApiClient)
                    .setResultCallback(result -> {
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "Error while trying to create new file contents");
                            showErrorDialog();
                            return;
                        }
                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                // write content to DriveContents
                                OutputStream outputStream = driveContents.getOutputStream();

                                try {
                                    BackupUtils.zip(BackupActivity.this, outputStream);
                                } catch (IOException e) {
                                    showErrorDialog();
                                    e.printStackTrace();
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("backup.zip")
                                        .setMimeType("application/zip")
                                        .build();

                                // create a file in selected folder
                                folder.createFile(googleApiClient, changeSet, driveContents)
                                        .setResultCallback(
                                                result1 -> {
                                                    if (!result1.getStatus().isSuccess()) {
                                                        Log.d(TAG,
                                                                "Error while trying to create the file");
                                                        showErrorDialog();
                                                        finish();
                                                        return;
                                                    }
                                                    //showSuccessDialog();
                                                    finish();
                                                });
                            }
                        }.start();
                    });
        }
    }

    private void openOnDrive(DriveId driveId) {
        driveId.asDriveFolder().getMetadata((googleApiClient)).setResultCallback(
                result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    String url = metadata.getAlternateLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
        );
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());

                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case 4:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    // Restart activity to apply changes
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BACKUP_FOLDER_KEY, folderPath);
        editor.apply();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.backup_failed,
                Toast.LENGTH_SHORT).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
}