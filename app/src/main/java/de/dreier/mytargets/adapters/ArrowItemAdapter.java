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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class ArrowItemAdapter extends ArrayAdapter<Arrow> {

    public ArrowItemAdapter(Context context) {
        super(context, 0, DatabaseManager.getInstance(context).getArrows());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.item_image, parent, false);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.image);
        TextView name = (TextView) convertView.findViewById(R.id.name);

        Arrow item = getItem(position);
        name.setText(item.name);
        img.setImageDrawable(new RoundedAvatarDrawable(item.image));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
}