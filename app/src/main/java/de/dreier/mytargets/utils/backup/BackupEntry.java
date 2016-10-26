package de.dreier.mytargets.utils.backup;

import java.util.Date;

public class BackupEntry {

    private String fileId;
    private Date modifiedDate;
    private long backupSize;

    public BackupEntry(String fileId, Date modifiedDate, long backupSize){
        this.fileId = fileId;
        this.modifiedDate = modifiedDate;
        this.backupSize = backupSize;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(long backupSize) {
        this.backupSize = backupSize;
    }

    public String getHumanReadableSize() {
        return "";//Utils.humanReadableByteCount(backupSize, true);
    }
}