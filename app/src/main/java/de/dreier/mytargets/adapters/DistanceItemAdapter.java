/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Distance;

public class DistanceItemAdapter extends ArrayAdapter<Distance> {

    public DistanceItemAdapter(Context context) {
        this(context, 10);
    }

    public DistanceItemAdapter(Context context, long distId) {
        super(context, R.layout.item_distance,
                DatabaseManager.getInstance(context).getDistances(distId));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        ((TextView) convertView).setText(getItem(position).toString(getContext()));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
}