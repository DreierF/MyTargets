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

package de.dreier.mytargets.features.settings.backup.provider

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.IIdProvider
import java.util.*

enum class EBackupLocation constructor(
        override var id: Long = 0,
        @StringRes
        private var nameRes: Int,
        @DrawableRes
        var drawableRes: Int) : IIdProvider {
    INTERNAL_STORAGE(1, R.string.internal_storage, R.drawable.ic_phone_android_grey600_24dp),
    EXTERNAL_STORAGE(2, R.string.external_storage, R.drawable.ic_micro_sd_card_grey600_24dp),
    GOOGLE_DRIVE(3, R.string.google_drive, R.drawable.ic_google_drive_grey600_24dp);

    fun createAsyncRestore(): IAsyncBackupRestore {
        return when (this) {
            EXTERNAL_STORAGE -> ExternalStorageBackup.AsyncRestore()
            GOOGLE_DRIVE -> GoogleDriveBackup.AsyncRestore()
            INTERNAL_STORAGE -> InternalStorageBackup.AsyncRestore()
        }
    }

    fun createBackup(): IBlockingBackup {
        return when (this) {
            EXTERNAL_STORAGE -> ExternalStorageBackup.Backup()
            GOOGLE_DRIVE -> GoogleDriveBackup.Backup()
            INTERNAL_STORAGE -> InternalStorageBackup.Backup()
        }
    }

    override fun toString(): String {
        return SharedApplicationInstance.getStr(nameRes)
    }

    fun needsStoragePermissions(): Boolean {
        return this == INTERNAL_STORAGE || this == EXTERNAL_STORAGE
    }

    companion object {
        val list: List<EBackupLocation>
            get() = if (ExternalStorageBackup.microSdCardPath != null) {
                Arrays.asList(INTERNAL_STORAGE, EXTERNAL_STORAGE, GOOGLE_DRIVE)
            } else {
                Arrays.asList(INTERNAL_STORAGE, GOOGLE_DRIVE)
            }
    }
}
