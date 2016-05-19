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

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.DonateActivity;

public class DonationAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;

    public DonationAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != getCount() - 1;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_donation, parent, false);
        }

        TextView desc = (TextView) convertView.findViewById(R.id.desc);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        if (position < 4) {
            String sku = DonateActivity.donations.get(position);
            price.setText(DonateActivity.prices.get(sku));
        } else {
            price.setText("");
        }

        switch (position) {
            case 0:
                desc.setText(R.string.donate_2);
                break;
            case 1:
                desc.setText(R.string.donate_5);
                break;
            case 2:
                desc.setText(R.string.donate_10);
                break;
            case 3:
                desc.setText(R.string.donate_20);
                break;
            case 4:
                desc.setText(R.string.donate_text);
                break;
        }
        return convertView;
    }
}
