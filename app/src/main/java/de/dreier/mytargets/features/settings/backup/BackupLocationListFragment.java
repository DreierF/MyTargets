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

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.settings.backup.provider.IAsyncBackupRestore;
import de.dreier.mytargets.fragments.SelectPureListItemFragmentBase;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static de.dreier.mytargets.features.settings.backup.BackupLocationListFragmentPermissionsDispatcher.applyBackupLocationWithCheck;

@RuntimePermissions
public class BackupLocationListFragment extends SelectPureListItemFragmentBase<EBackupLocation> {

    private IAsyncBackupRestore backup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAdapter.setList(EBackupLocation.getList());
        ToolbarUtils.showHomeAsUp(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(SelectableViewHolder<EBackupLocation> holder, EBackupLocation item) {
        super.onClick(holder, item);
        if (item.needsStoragePermissions()) {
            applyBackupLocationWithCheck(this, holder, item);
        } else {
            applyBackupLocation(holder, item);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void applyBackupLocation(SelectableViewHolder<EBackupLocation> holder, EBackupLocation item) {
         backup = item.createAsyncRestore();
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content(R.string.connecting)
                .progress(true, 0)
                .show();
        backup.connect(getActivity(), new IAsyncBackupRestore.ConnectionListener() {
            @Override
            public void onConnected() {
                dialog.dismiss();
                BackupLocationListFragment.super.onClick(holder, item);
            }

            @Override
            public void onConnectionSuspended() {
                dialog.dismiss();
                Snackbar.make(binding.getRoot(), R.string.connection_failed, LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        backup.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BackupLocationListFragmentPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

}