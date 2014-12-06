package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import de.dreier.mytargets.R;

public class TargetItemAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context mContext;
    public static String[] targets = {"WA 40cm", "WA 60cm",
            "WA 80cm", "WA 120cm", "WA 3er Spot 20cm", "WA Spot 40cm", "WA Spot 60cm", "WA Spot 80cm",
            "WA Field 40cm", "DFBV Spiegel 40cm", "DFBV Spiegel Spot 40cm"};

    private static int[] targets_drawable = {R.drawable.wa, R.drawable.wa,
            R.drawable.wa, R.drawable.wa, R.drawable.wa_spot, R.drawable.wa_spot, R.drawable.wa_spot, R.drawable.wa_spot,
            R.drawable.wa_field, R.drawable.dfbv_spiegel, R.drawable.dfbv_spiegel_spot};

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
			v = vi.inflate(R.layout.target_item, null);
		}

        ImageView img = (ImageView) v.findViewById(R.id.targetImage);
        TextView desc = (TextView) v.findViewById(R.id.targetDescription);

        img.setImageDrawable(mContext.getResources().getDrawable(targets_drawable[position]));
        desc.setText(targets[position]);
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position,convertView,parent);
	}
}