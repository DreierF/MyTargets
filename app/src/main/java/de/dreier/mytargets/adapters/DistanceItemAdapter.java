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

public class DistanceItemAdapter extends ArrayAdapter<Integer> {

    public DistanceItemAdapter(Context context) {
        this(context, 10);
    }

    public DistanceItemAdapter(Context context, int dist) {
        super(context, R.layout.distance_text_item,
                DatabaseManager.getInstance(context).getDistances(dist));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        ((TextView) convertView).setText(getItem(position) + "m");
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position);
    }
}