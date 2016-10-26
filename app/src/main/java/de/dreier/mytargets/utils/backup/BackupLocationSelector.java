/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils.backup;

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