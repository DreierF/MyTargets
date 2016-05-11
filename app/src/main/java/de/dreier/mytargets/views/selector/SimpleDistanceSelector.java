/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.Distance;

public class SimpleDistanceSelector extends SelectorBase<Distance> {

    private static final int SIMPLE_DISTANCE_REQUEST_CODE = 2;

    @Bind(android.R.id.text1)
    TextView distance;

    private int index;

    public SimpleDistanceSelector(Context context) {
        this(context, null);
    }

    public SimpleDistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_simple_text);
        defaultActivity = ItemSelectActivity.DistanceActivity.class;
        requestCode = SIMPLE_DISTANCE_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        distance.setText(item.toString(getContext()));
    }

    public void setItemIndex(int index) {
        this.index = index;
    }

    @Override
    public Intent getDefaultIntent() {
        Intent i = super.getDefaultIntent();
        i.putExtra("index", index);
        return i;
    }

    private static final String TAG = "SimpleDistanceSelector";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            Log.i(TAG, "onActivityResult:intentData "+intentData);
            if (intentData != null && intentData.getInt("index") == index) {
                Log.i(TAG, "onActivityResult:index "+index);
                final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
                setItem(Parcels.unwrap(parcelable));
            }
        }
    }
}
