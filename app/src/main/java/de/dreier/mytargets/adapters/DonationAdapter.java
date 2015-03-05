package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.dreier.mytargets.R;

/**
 * Created by Florian on 05.03.2015.
 */
public class DonationAdapter extends BaseAdapter {

    private final Context mContext;
    private final boolean mSupported;
    private final LayoutInflater mInflater;

    public DonationAdapter(Context context, boolean supported) {
        mContext = context;
        mSupported = supported;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSupported?5:4;
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
            convertView = mInflater.inflate(R.layout.donation_item, parent, false);
        }
        TextView desc = (TextView) convertView.findViewById(R.id.desc);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        switch (position) {
            case 0:
                desc.setText(R.string.donate_2);
                price.setText(R.string.donate_2_price);
                break;
            case 1:
                desc.setText(R.string.donate_5);
                price.setText(R.string.donate_5_price);
                break;
            case 2:
                desc.setText(R.string.donate_10);
                price.setText(R.string.donate_10_price);
                break;
            case 3:
                desc.setText(R.string.donate_20);
                price.setText(R.string.donate_20_price);
                break;
            case 4:
                desc.setText(R.string.donate_infinite);
                price.setText(R.string.donate_infinite_price);
                break;
        }
        return convertView;
    }
}
