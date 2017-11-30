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
import android.view.View;

import de.dreier.mytargets.base.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.shared.models.db.Signature;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;

public class ScoreboardActivity extends SimpleFragmentActivityBase {

    @VisibleForTesting
    public static final String TRAINING_ID = "training_id";
    @VisibleForTesting
    public static final String ROUND_ID = "round_id";

    /**
     * TODO:
     * v Make multi pages work
     * v Make properties tables not span whole page
     * v Add PDF export/share option (#21)
     * v File name should contain date (#43)
     * v Fix image share option (Always share PDF! Make it adjustable in settings!)
     * v Reimplement signature lines
     * - Add handwritten signature (#321)
     * v Add progress indicator when opening scoreboard
     * x Add progress dialog when hitting print
     * - Implement other scoreboard layout (#246)
     * - Add settings screen to switch between them (compare google keyboard layout chooser?)
     * v #322
     * v Remove HTMLBuilder
     * v Fixes #288
     */

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

    public void sign(Signature signature, String defaultName, View sharedElement) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, SignatureFragment
                        .newInstance(signature, defaultName))
                .addToBackStack(null)
                .commit();
    }

    public void back() {
        getSupportFragmentManager().popBackStack();
    }
}
