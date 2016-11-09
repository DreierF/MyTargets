package de.dreier.mytargets.shared.analysis.aggregation;

import java.util.List;

import de.dreier.mytargets.shared.models.Shot;

public interface IAggregationStrategy {
    void registerOnAggregationResultListener(final ClusterStrategy.OnAggregationResult onAggregationResult);
    void unregisterOnAggregationResultListener(final ClusterStrategy.OnAggregationResult onAggregationResult);

    void calculate(List<Shot> shots);

    interface OnAggregationResult {
        void onResult();
        void onProgressUpdate(int paramInt);
    }

    class AggregationDrawable {
    }
}
