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

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class TargetItemAdapter extends BaseAdapter {
    private final Context mContext;
    private Target target;

    public TargetItemAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return TargetFactory.getList(mContext).size();
    }

    @Override
    public Object getItem(int i) {
        return target == null ? TargetFactory.getList(mContext).get(i) : target;
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
            v = vi.inflate(R.layout.item_image, parent, false);
        }

        ImageView img = (ImageView) v.findViewById(R.id.image);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView details = (TextView) v.findViewById(R.id.details);
        details.setVisibility(View.VISIBLE);

        Target item = (Target) getItem(position);
        img.setImageDrawable(item);
        name.setText(item.name + " (" + item.size.toString(mContext) + ")");
        details.setText(item.getScoringStyles().get(item.scoringStyle));
        return v;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}