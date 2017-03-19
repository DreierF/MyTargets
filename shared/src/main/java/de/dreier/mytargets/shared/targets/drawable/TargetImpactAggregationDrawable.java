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

package de.dreier.mytargets.shared.targets.drawable;

import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;

import static de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy.NONE;

public class TargetImpactAggregationDrawable extends TargetImpactDrawable implements IAggregationStrategy.OnAggregationResult {

    private final List<IAggregationStrategy> faceAggregations = new ArrayList<>();
    private int resultsMissing = 0;

    public TargetImpactAggregationDrawable(Target target) {
        super(target);
        setAggregationStrategy(NONE);
    }

    public void setAggregationStrategy(EAggregationStrategy aggregation) {
        faceAggregations.clear();
        for (int i = 0; i < model.getFaceCount(); i++) {
            IAggregationStrategy strategy = aggregation.newInstance();
            strategy.setOnAggregationResultListener(this);
            faceAggregations.add(strategy);
        }
        setColor(0xAAAAAAAA);
        recalculateAggregation();
    }

    public void setColor(@ColorInt int color) {
        for (IAggregationStrategy faceAggregation : faceAggregations) {
            faceAggregation.setColor(color);
        }
    }

    @Override
    public void onResult() {
        resultsMissing--;
        if (resultsMissing == 0) {
            invalidateSelf();
        }
    }

    @Override
    protected void onPostDraw(CanvasWrapper canvas, int faceIndex) {
        super.onPostDraw(canvas, faceIndex);
        final IAggregationResultRenderer result = faceAggregations.get(faceIndex).getResult();
        result.onDraw(canvas);
    }

    @Override
    public void cleanup() {
        for (IAggregationStrategy cluster : faceAggregations) {
            cluster.cleanup();
        }
    }

    @Override
    public void notifyArrowSetChanged() {
        super.notifyArrowSetChanged();
        recalculateAggregation();
    }

    private void recalculateAggregation() {
        resultsMissing = model.getFaceCount();
        for (int faceIndex = 0; faceIndex < model.getFaceCount(); faceIndex++) {
            ArrayList<Shot> combinedList = new ArrayList<>();
            combinedList.addAll(transparentShots.get(faceIndex));
            combinedList.addAll(shots.get(faceIndex));
            faceAggregations.get(faceIndex).calculate(combinedList);
        }
    }
}
