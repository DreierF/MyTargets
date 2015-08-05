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
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;

public class EnvironmentItemAdapter extends BaseAdapter {
    private final Context mContext;
    private Environment environment;

    public EnvironmentItemAdapter(Context context) {
        mContext = context;
        environment = new Environment(EWeather.SUNNY, 0, 0);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return environment;
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
        TextView desc = (TextView) v.findViewById(R.id.name);
        TextView details = (TextView) v.findViewById(R.id.details);

        img.setImageResource(environment.weather.getDrawable());
        desc.setText(environment.weather.getName());
        String direction = mContext.getResources()
                .getStringArray(R.array.wind_directions)[environment.windDirection];
        details.setText(environment.windSpeed + " Btf " + direction);
        details.setVisibility(View.VISIBLE);
        return v;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }
}