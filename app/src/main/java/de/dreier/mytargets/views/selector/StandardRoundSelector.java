/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;

import org.parceler.Parcels;

import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.StandardRoundActivity;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;

public class StandardRoundSelector extends ImageSelectorBase<StandardRound> {

    private static final int STANDARD_ROUND_REQUEST_CODE = 10;
    private static final int SR_TARGET_REQUEST_CODE = 11;

    public StandardRoundSelector(Context context) {
        this(context, null);
    }

    public StandardRoundSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        image.setFocusable(true);
        image.setClickable(true);
        defaultActivity = StandardRoundActivity.class;
        requestCode = STANDARD_ROUND_REQUEST_CODE;
    }

    @Override
    public void setOnActivityResultContext(Fragment fragment) {
        super.setOnActivityResultContext(fragment);
        image.setOnClickListener(v -> {
            final StandardRound item = getSelectedItem();
            Target target = item.getRounds().get(0).targetTemplate;
            if (target.id < 7 || target.id == 10 || target.id == 11) {
                Intent i = new Intent(getContext(), ItemSelectActivity.TargetActivity.class);
                i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(target));
                i.putExtra(TargetFragment.TYPE_FIXED, true);
                fragment.startActivityForResult(i, SR_TARGET_REQUEST_CODE);
            } else {
                fragment.startActivityForResult(getDefaultIntent(), requestCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SR_TARGET_REQUEST_CODE) {
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            final Target st = Parcels.unwrap(parcelable);
            StandardRound item = getSelectedItem();
            for (RoundTemplate template : item.getRounds()) {
                Dimension size = template.target.size;
                template.target = new Target(st.id, st.scoringStyle, size);
            }
            setItem(item);
        }
    }

    @Override
    protected boolean isImageSelectable() {
        return true;
    }

    public void setItemId(long standardRound) {
        setItem(new StandardRoundDataSource().get(standardRound));
    }
}
