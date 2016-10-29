package de.dreier.mytargets.utils.backup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.parceler.ParcelConstructor;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IImageProvider;

public enum EBackupLocation implements IIdProvider, IImageProvider {
    INTERNAL_STORAGE(1, R.string.internal_storage, R.drawable.ic_phone_android_black_24dp),
    EXTERNAL_STORAGE(2, R.string.external_storage, R.drawable.ic_micro_sd_card_24dp),
    GOOGLE_DRIVE(3, R.string.google_drive, R.drawable.ic_google_drive_24dp),
    DROPBOX(4, R.string.dropbox, R.drawable.ic_dropbox_24dp);

    int id;
    int drawable;
    int name;

    @ParcelConstructor
    EBackupLocation(int id, @StringRes int name, @DrawableRes int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    public static List<EBackupLocation> getList() {
        if (ExternalStorageBackup.getMicroSdCardPath() != null) {
            return Arrays.asList(INTERNAL_STORAGE, EXTERNAL_STORAGE, GOOGLE_DRIVE, DROPBOX);
        } else {
            return Arrays.asList(INTERNAL_STORAGE, GOOGLE_DRIVE, DROPBOX);
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public Backup createBackup() {
        switch (this) {
            case INTERNAL_STORAGE:
                return new LocalDeviceBackup();
            case EXTERNAL_STORAGE:
                return new ExternalStorageBackup();
            case GOOGLE_DRIVE:
                return new GoogleDriveBackup();
            default:
                return new DropboxBackup();
        }
    }

    @Override
    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(drawable);
    }

    @Override
    public String getName() {
        return ApplicationInstance.get(name);
    }
}
