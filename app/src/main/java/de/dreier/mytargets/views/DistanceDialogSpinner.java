/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DistanceItemAdapter;
import de.dreier.mytargets.utils.TextInputDialog;


public class DistanceDialogSpinner extends DialogSpinner
        implements TextInputDialog.OnClickListener {

    public DistanceDialogSpinner(Context context) {
        super(context);
        init();
    }

    public DistanceDialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setTitle(R.string.distance);
        setAdapter(new DistanceItemAdapter(getContext()));
        setAddButton(null, R.string.custom, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TextInputDialog.Builder(getContext())
                        .setTitle(R.string.distance)
                        .setOnClickListener(DistanceDialogSpinner.this)
                        .show();
            }
        });
    }

    @Override
    public void onCancelClickListener() {
    }

    @Override
    public void onOkClickListener(String input) {
        input = input.replaceAll("[^0-9]", "");
        int dist = Integer.parseInt(input);
        setAdapter(new DistanceItemAdapter(getContext(), dist));
        setItemId(dist);
    }
}
