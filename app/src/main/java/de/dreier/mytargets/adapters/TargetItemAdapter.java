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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.models.IdProvider;

public class TargetItemAdapter extends BaseAdapter {
    private final Context mContext;

    public static List<Target> targets;
    static {
        targets = new ArrayList<>();
        targets.add(new Target("WA", R.drawable.wa));
        targets.add(new Target("WA Spot 5-10", R.drawable.wa_spot_5));
        targets.add(new Target("WA Spot 6-10", R.drawable.wa_spot_6));
        targets.add(new Target("WA 3er Spot", R.drawable.wa_spot_6));
        targets.add(new Target("WA Field",  R.drawable.wa_field));
        targets.add(new Target("DFBV Spiegel", R.drawable.dfbv_spiegel));
        targets.add(new Target("DFBV Spiegel Spot", R.drawable.dfbv_spiegel_spot));
        targets.add(new Target("DFBV Field", R.drawable.dfbv_field));
    }
    public static class Target extends IdProvider {
        public String name;
        public int drawableRes;

        public Target(String name, int drawableRes) {
            this.id = targets.size();
            this.name = name;
            this.drawableRes = drawableRes;
        }
    }

    public TargetItemAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return targets.size();
    }

    @Override
    public Object getItem(int i) {
        return targets.get(i);
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

        img.setImageDrawable(mContext.getResources().getDrawable(targets.get(position).drawableRes));
        desc.setText(targets.get(position).name);
        return v;
    }
}