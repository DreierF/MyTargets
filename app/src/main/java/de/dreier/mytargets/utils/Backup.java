package de.dreier.mytargets.utils;

import com.google.android.gms.drive.DriveId;

import org.joda.time.DateTime;

public class Backup {

    private DriveId driveId;
    private DateTime modifiedDate;
    private long backupSize;

    public Backup(DriveId driveId, DateTime modifiedDate, long backupSize){
        this.driveId = driveId;
        this.modifiedDate = modifiedDate;
        this.backupSize = backupSize;
    }

    public DriveId getDriveId() {
        return driveId;
    }

    public void setDriveId(DriveId driveId) {
        this.driveId = driveId;
    }

    public DateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(DateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(long backupSize) {
        this.backupSize = backupSize;
    }
}