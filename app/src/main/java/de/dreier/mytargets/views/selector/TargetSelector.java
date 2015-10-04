/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.target.Target;

public class TargetSelector extends SelectorBase<Target> {

    @Bind(R.id.image)
    ImageView img;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.details)
    TextView details;

    public TargetSelector(Context context) {
        this(context, null);
        ButterKnife.bind(this, mView);
    }

    public TargetSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_target_face);
        ButterKnife.bind(this, mView);
        setOnClickActivity(ItemSelectActivity.TargetActivity.class);
    }

    @Override
    protected void bindView() {
        details.setVisibility(View.VISIBLE);
        img.setImageDrawable(item);
        name.setText(String.format("%s (%s)", item.name, item.size.toString(getContext())));
        details.setText(item.getScoringStyles().get(item.scoringStyle));
    }
}
