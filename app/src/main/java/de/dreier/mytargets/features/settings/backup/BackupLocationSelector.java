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
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.databinding.PreferenceImageDetailsBinding;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.views.selector.SelectorBase;

public class BackupLocationSelector extends SelectorBase<EBackupLocation> {

    public static final int BACKUP_REQUEST_CODE = 5;

    protected PreferenceImageDetailsBinding binding;

    public BackupLocationSelector(Context context) {
        this(context, null);
    }

    public BackupLocationSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.preference_image_details);
        defaultActivity = ItemSelectActivity.BackupLocationActivity.class;
        requestCode = BACKUP_REQUEST_CODE;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding = DataBindingUtil.bind(view);
    }

    @Override
    protected void bindView() {
        binding.name.setText(R.string.backup_location);
        binding.summary.setText(item.getName());
        binding.image.setImageDrawable(item.getDrawable(getContext()));
        invalidate();
    }
}