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

package de.dreier.mytargets.features.scoreboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;

public class ScoreboardActivity extends SimpleFragmentActivityBase {

    @VisibleForTesting
    public static final String TRAINING_ID = "training_id";
    @VisibleForTesting
    public static final String ROUND_ID = "round_id";

    @NonNull
    public static IntentWrapper getIntent(long trainingId) {
        return getIntent(trainingId, -1);
    }

    @NonNull
    public static IntentWrapper getIntent(long trainingId, long roundId) {
        return new IntentWrapper(ScoreboardActivity.class)
                .with(TRAINING_ID, trainingId)
                .with(ROUND_ID, roundId);
    }

    @Override
    protected Fragment instantiateFragment() {
        return new ScoreboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
    }
}
