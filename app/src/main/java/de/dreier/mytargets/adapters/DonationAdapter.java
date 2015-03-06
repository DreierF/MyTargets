package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DonateDialogFragment;

/**
 * Created by Florian on 05.03.2015.
 */
public class DonationAdapter extends BaseAdapter {

    private final boolean mSupported;
    private final LayoutInflater mInflater;
    private Context mContext;

    public DonationAdapter(Context context, boolean supported) {
        mContext = context;
        mSupported = supported;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSupported ? 5 : 4;
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
        String sku = DonateDialogFragment.donations.get(position);
        if(position==4) {
            price.setText(mContext.getString(R.string.monthly, DonateDialogFragment.prices.get(sku)));
        } else {
            price.setText(DonateDialogFragment.prices.get(sku));
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
                desc.setText(R.string.donate_infinite);
                break;
        }
        return convertView;
    }
}
