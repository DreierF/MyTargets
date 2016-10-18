/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;

import de.dreier.mytargets.activities.StandardRoundActivity;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;

public class StandardRoundSelector extends ImageSelectorBase<StandardRound> {

    private static final int STANDARD_ROUND_REQUEST_CODE = 10;

    public StandardRoundSelector(Context context) {
        this(context, null);
    }

    public StandardRoundSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = StandardRoundActivity.class;
        requestCode = STANDARD_ROUND_REQUEST_CODE;
    }

    public void setItemId(long standardRoundId) {
        StandardRound standardRound = StandardRound.get(standardRoundId);
        // If the round has been removed, choose default one
        if (standardRound == null) {
            standardRound = StandardRound.get(32L);
        }
        setItem(standardRound);
    }
}
