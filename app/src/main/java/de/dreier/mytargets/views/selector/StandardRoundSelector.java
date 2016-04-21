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

import org.parceler.Parcels;

import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.StandardRoundActivity;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;

public class StandardRoundSelector extends ImageSelectorBase<StandardRound> {

    public StandardRoundSelector(Context context) {
        this(context, null);
    }

    public StandardRoundSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        image.setFocusable(true);
        image.setClickable(true);
        setOnClickActivity(StandardRoundActivity.class);
    }

    @OnClick(R.id.image)
    public void onSelectAlternativeTarget() {
        Target target = item.getRounds().get(0).targetTemplate;
        if (target.id < 7 || target.id == 10 || target.id == 11) {
            Intent i = new Intent(getContext(), ItemSelectActivity.TargetActivity.class);
            i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(target));
            i.putExtra(TargetFragment.TYPE_FIXED, true);
            startIntent(i, data -> {
                Target st = Parcels.unwrap(data.getParcelableExtra(ItemSelectActivity.ITEM));
                for (RoundTemplate template : item.getRounds()) {
                    Diameter size = template.target.size;
                    template.target = new Target(st.id, st.scoringStyle, size);
                }
                setItem(item);
            });
        } else {
            Intent i = new Intent(getContext(), StandardRoundActivity.class);
            i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(item));
            startIntent(i, data -> setItem(Parcels.unwrap(data.getParcelableExtra(ItemSelectActivity.ITEM))));
        }
    }

    public void setItemId(long standardRound) {
        setItem(new StandardRoundDataSource(getContext()).get(standardRound));
    }
}
