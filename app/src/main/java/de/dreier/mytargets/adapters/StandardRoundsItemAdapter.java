/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.dreier.mytargets.shared.models.StandardRound;

public class StandardRoundsItemAdapter extends BaseAdapter {
    private final Context mContext;

    private StandardRound standardRound;

    public StandardRoundsItemAdapter(Context context, StandardRound round) {
        mContext = context;
        standardRound = round;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return standardRound;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView desc = (TextView) v.findViewById(android.R.id.text1);

        desc.setText(standardRound.getName());
        return v;
    }

    public void setStandardRound(StandardRound standardRound) {
        this.standardRound = standardRound;
    }

    public StandardRound getStandardRound() {
        return standardRound;
    }
}