package com.bignerdranch.android.recyclerviewchoicemode;

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;

public abstract class MultiSelectorBindingHolder extends RebindReportingHolder implements SelectableHolder {
    private final MultiSelector mMultiSelector;

    public MultiSelectorBindingHolder(View itemView, MultiSelector multiSelector) {
        super(itemView);
        mMultiSelector = multiSelector;
    }

    @Override
    protected void onRebind() {
        if (mMultiSelector != null)
            mMultiSelector.bindHolder(this, getPosition(), getItemId());
    }
}
