/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.shared.analysis.aggregation;

import java.util.List;

public class NoneStrategy implements IAggregationStrategy {
    @Override
    public void setOnAggregationResultListener(OnAggregationResult onAggregationResult) {

    }

    @Override
    public IAggregationResultRenderer getResult() {
        return null;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void setColor(int color) {

    }

    @Override
    public void calculate(List list) {

    }
}
