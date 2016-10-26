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
    LOCAL_DEVICE(1, R.string.local_device, R.drawable.ic_phone_android_black_24dp),
    GOOGLE_DRIVE(2, R.string.google_drive, R.drawable.ic_google_drive_24dp);

    int id;
    int drawable;
    int name;

    @ParcelConstructor
    EBackupLocation(int id, @StringRes int name, @DrawableRes int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    @Override
    public long getId() {
        return id;
    }

    public Backup createBackup() {
        switch (this) {
            case LOCAL_DEVICE:
                return new LocalDeviceBackup();
            default:
                return new GoogleDriveBackup();
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

    public static List<EBackupLocation> getList() {
        return Arrays.asList(LOCAL_DEVICE, GOOGLE_DRIVE);
    }
}
