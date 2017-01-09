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

package de.dreier.mytargets.features.settings.backup.provider;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.parceler.ParcelConstructor;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.shared.models.IIdProvider;

public enum EBackupLocation implements IIdProvider {
    INTERNAL_STORAGE(1, R.string.internal_storage, R.drawable.ic_phone_android_grey600_24dp),
    EXTERNAL_STORAGE(2, R.string.external_storage, R.drawable.ic_micro_sd_card_grey600_24dp),
    GOOGLE_DRIVE(3, R.string.google_drive, R.drawable.ic_google_drive_grey600_24dp);

    Long id;
    int drawable;
    int name;

    @ParcelConstructor
    EBackupLocation(long id, @StringRes int name, @DrawableRes int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    public static List<EBackupLocation> getList() {
        if (ExternalStorageBackup.getMicroSdCardPath() != null) {
            return Arrays.asList(INTERNAL_STORAGE, EXTERNAL_STORAGE, GOOGLE_DRIVE);
        } else {
            return Arrays.asList(INTERNAL_STORAGE, GOOGLE_DRIVE);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public IAsyncBackupRestore createAsyncRestore() {
        switch (this) {
            case EXTERNAL_STORAGE:
                return new ExternalStorageBackup.AsyncRestore();
            case GOOGLE_DRIVE:
                return new GoogleDriveBackup.AsyncRestore();
            case INTERNAL_STORAGE:
            default:
                return new InternalStorageBackup.AsyncRestore();
        }
    }

    public IBlockingBackup createBackup() {
        switch (this) {
            case EXTERNAL_STORAGE:
                return new ExternalStorageBackup.Backup();
            case GOOGLE_DRIVE:
                return new GoogleDriveBackup.Backup();
            case INTERNAL_STORAGE:
            default:
                return new InternalStorageBackup.Backup();
        }
    }

    public int getDrawableRes() {
        return drawable;
    }

    @Override
    public String toString() {
        return ApplicationInstance.get(name);
    }

    public boolean needsStoragePermissions() {
        return this == INTERNAL_STORAGE || this == EXTERNAL_STORAGE;
    }
}
