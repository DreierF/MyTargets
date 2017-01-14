/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;

import de.dreier.mytargets.features.training.standardround.StandardRoundActivity;
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

    public void setItemId(Long standardRoundId) {
        StandardRound standardRound = StandardRound.get(standardRoundId);
        // If the round has been removed, choose default one
        if (standardRound == null) {
            standardRound = StandardRound.get(32L);
        }
        setItem(standardRound);
    }
}
