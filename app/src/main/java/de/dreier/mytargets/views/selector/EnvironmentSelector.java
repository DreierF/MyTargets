/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.Environment;

public class EnvironmentSelector extends SelectorBase<Environment> {

    public EnvironmentSelector(Context context) {
        this(context, null);
    }

    public EnvironmentSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        setOnClickActivity(ItemSelectActivity.Environment.class);
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView desc = (TextView) mView.findViewById(R.id.name);
        TextView details = (TextView) mView.findViewById(R.id.details);

        img.setImageResource(item.weather.getDrawable());
        desc.setText(item.weather.getName());
        String direction = getContext().getResources()
                .getStringArray(R.array.wind_directions)[item.windDirection];
        String description =
                getContext().getString(R.string.wind) + ": " + item.windSpeed + " Btf " +
                        direction;
        if (!TextUtils.isEmpty(item.location)) {
            description +=
                    "\n" + getContext().getString(R.string.location) + ": " + item.location;
        }
        details.setText(description);
        details.setVisibility(View.VISIBLE);
    }
}
