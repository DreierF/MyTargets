/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.StandardRoundActivity;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;

public class StandardRoundSelector extends SelectorBase<StandardRound> {

    public StandardRoundSelector(Context context) {
        this(context, null);
    }

    public StandardRoundSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_standard_round);
        mView.findViewById(R.id.content).setOnClickListener(v -> {
            Intent i = new Intent(getContext(), StandardRoundActivity.class);
            i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(item));
            startIntent(i, data -> setItem((StandardRound) data.getSerializableExtra(ItemSelectActivity.ITEM)));
        });
        mView.findViewById(R.id.image).setOnClickListener(v -> {
            Target target = item.getRounds().get(0).targetTemplate;
            if (target.id < 7 || target.id == 10 || target.id == 11) {
                Intent i = new Intent(getContext(), ItemSelectActivity.TargetActivity.class);
                i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(target));
                i.putExtra(TargetFragment.TYPE_FIXED, true);
                startIntent(i, data -> {
                    Target st = (Target) data.getSerializableExtra(ItemSelectActivity.ITEM);
                    for (RoundTemplate template : item.getRounds()) {
                        Diameter size = template.target.size;
                        template.target = new Target(st.id, st.scoringStyle, size);
                    }
                    setItem(item);
                });
            } else {
                Intent i = new Intent(getContext(), StandardRoundActivity.class);
                i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(item));
                startIntent(i, data -> setItem((StandardRound) data.getSerializableExtra(ItemSelectActivity.ITEM)));
            }
        });
    }

    @Override
    protected void bindView() {
        TextView name = (TextView) mView.findViewById(android.R.id.text1);
        TextView desc = (TextView) mView.findViewById(android.R.id.text2);
        ImageView image = (ImageView) mView.findViewById(R.id.image);

        name.setText(item.getName());
        desc.setText(item.getDescription(getContext()));
        RoundTemplate firstRound = item.getRounds().get(0);
        image.setImageDrawable(firstRound.target.getDrawable());
    }

    public void setItemId(long standardRound) {
        setItem(new StandardRoundDataSource(getContext()).get(standardRound));
    }
}
