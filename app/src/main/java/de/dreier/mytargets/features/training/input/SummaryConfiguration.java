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

package de.dreier.mytargets.features.training.input;

public class SummaryConfiguration {
    public boolean showEnd;
    public boolean showRound;
    public boolean showTraining;
    public boolean showAverage;
    public ETrainingScope averageScope = ETrainingScope.ROUND;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SummaryConfiguration that = (SummaryConfiguration) o;

        return showEnd == that.showEnd
                && showRound == that.showRound
                && showTraining == that.showTraining
                && showAverage == that.showAverage
                && averageScope == that.averageScope;

    }

    @Override
    public int hashCode() {
        int result = (showEnd ? 1 : 0);
        result = 31 * result + (showRound ? 1 : 0);
        result = 31 * result + (showTraining ? 1 : 0);
        result = 31 * result + (showAverage ? 1 : 0);
        result = 31 * result + averageScope.hashCode();
        return result;
    }
}
