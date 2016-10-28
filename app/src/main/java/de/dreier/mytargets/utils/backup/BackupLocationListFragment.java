package de.dreier.mytargets.utils.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.fragments.SelectPureListItemFragmentBase;
import de.dreier.mytargets.utils.ToolbarUtils;

public class BackupLocationListFragment extends SelectPureListItemFragmentBase<EBackupLocation> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAdapter.setList(EBackupLocation.getList());
        ToolbarUtils.showHomeAsUp(this);
        return binding.getRoot();
    }
}
