package com.bignerdranch.android.recyclerviewchoicemode;

import android.support.v7.view.ActionMode;
import android.view.Menu;

public abstract class ModalMultiSelectorCallback implements ActionMode.Callback {
    private MultiSelector mMultiSelector;

    private boolean mClearOnPrepare = false;//TODO was true

    public ModalMultiSelectorCallback(MultiSelector multiSelector) {
        mMultiSelector = multiSelector;
    }

    public int getSelectedCount() {
        return mMultiSelector.getSelectedPositions().size();
    }

    public boolean shouldClearOnPrepare() {
        return mClearOnPrepare;
    }

    public void setClearOnPrepare(boolean clearOnPrepare) {
        mClearOnPrepare = clearOnPrepare;
    }

    public MultiSelector getMultiSelector() {
        return mMultiSelector;
    }

    public void setMultiSelector(MultiSelector multiSelector) {
        mMultiSelector = multiSelector;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        if (mClearOnPrepare) {
            mMultiSelector.clearSelections();
        }
        mMultiSelector.setSelectable(true);
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mMultiSelector.clearSelections();
        mMultiSelector.setSelectable(false);
    }
}
