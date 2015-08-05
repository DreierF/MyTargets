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
import de.dreier.mytargets.fragments.DonateDialogFragment;

public class DonationAdapter extends BaseAdapter {

    private final boolean mSupported;
    private final LayoutInflater mInflater;
    private final Context mContext;

    public DonationAdapter(Context context, boolean supported) {
        mContext = context;
        mSupported = supported;
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
        return mSupported ? 6 : 5;
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
        if (position == 4) {
            price.setText(mContext.getString(R.string.monthly,
                    DonateDialogFragment.prices
                            .get(DonateDialogFragment.DONATION_INFINITE)));
        } else if (position < 4) {
            String sku = DonateDialogFragment.donations.get(position);
            price.setText(DonateDialogFragment.prices.get(sku));
        } else {
            price.setText("");
        }

        float density = mContext.getResources().getDisplayMetrics().scaledDensity;
        desc.setTextSize(density * 5);
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
                desc.setText(R.string.donate_infinite);
                break;
            case 5:
                desc.setText(R.string.donate_text);
                desc.setTextSize(density * 4);
                break;
        }
        return convertView;
    }
}
