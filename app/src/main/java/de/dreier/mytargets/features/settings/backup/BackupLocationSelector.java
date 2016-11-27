/*
 * Copyright (C) 2016 Florian Dreier
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

import android.content.Context;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.views.selector.ImageSelectorBase;

public class BackupLocationSelector extends ImageSelectorBase<EBackupLocation> {

    public static final int BACKUP_REQUEST_CODE = 5;

    public BackupLocationSelector(Context context) {
        this(context, null);
    }

    public BackupLocationSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.BackupLocationActivity.class;
        requestCode = BACKUP_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        super.bindView();
        setTitle(R.string.backup_location);
    }
}
