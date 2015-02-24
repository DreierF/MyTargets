package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;

public class TargetItemAdapter extends BaseAdapter {
    private final Context mContext;
    public static final String[] targets = {"WA", "WA Spot 5-10", "WA Spot 6-10", "WA 3er Spot",
            "WA Field", "DFBV Spiegel", "DFBV Spiegel Spot", "DFBV Field"};

    private static final int[] targets_drawable = {R.drawable.wa, R.drawable.wa_spot_5, R.drawable.wa_spot_6, R.drawable.wa_spot_6,
            R.drawable.wa_field, R.drawable.dfbv_spiegel, R.drawable.dfbv_spiegel_spot, R.drawable.dfbv_field};

	public TargetItemAdapter(Context context) {
        mContext = context;
	}

    @Override
    public int getCount() {
        return targets.length;
    }

    @Override
    public Object getItem(int i) {
        return targets[i];
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
			v = vi.inflate(R.layout.image_item, parent, false);
		}

        ImageView img = (ImageView) v.findViewById(R.id.image);
        TextView desc = (TextView) v.findViewById(R.id.name);

        img.setImageDrawable(mContext.getResources().getDrawable(targets_drawable[position]));
        desc.setText(targets[position]);
		return v;
	}
}