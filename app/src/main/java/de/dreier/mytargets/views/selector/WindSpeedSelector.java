/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.models.WindSpeed;

public class WindSpeedSelector extends SelectorBase<WindSpeed> {

    public WindSpeedSelector(Context context) {
        this(context, null);
    }

    public WindSpeedSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_simple_text);
        setOnClickActivity(ItemSelectActivity.WindSpeedActivity.class);
    }

    @Override
    protected void bindView() {
        TextView name = (TextView) mView.findViewById(android.R.id.text1);
        name.setText(item.name);
    }

    public void setItemId(long speed) {
        setItem(WindSpeed.getList(getContext()).get((int) speed));
    }
}
