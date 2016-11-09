package de.dreier.mytargets.shared.analysis.aggregation;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Shot;

public class ClusterStrategy implements IAggregationStrategy {
    private final ArrayList<Cluster> clusters;
    private final ArrayList<PointF> data;
    private final ArrayList<OnAggregationResult> resultListeners;
    private AsyncTask<ArrayList<PointF>, Integer, Void> computeClustersTask;
    private boolean isDirty;
    private final int nClusters;
    private boolean overflow;

    public ClusterStrategy(int nClusters) {
        this.resultListeners = new ArrayList<>();
        this.clusters = new ArrayList<>();
        this.data = new ArrayList<>();
        this.isDirty = false;
        this.isDirty = true;
        this.overflow = false;
        this.nClusters = nClusters;
    }

    private void restartComputationTask() {
        if (computeClustersTask != null) {
            computeClustersTask.cancel(true);
        }
        computeClustersTask = new ComputeClustersTask().execute(data);
    }

    @Override
    public void calculate(List<Shot> shots) {
        reset();
        Stream.of(shots).forEach(shot -> add(shot.x, shot.y));
        restartComputationTask();
    }

    public void add(final float n1, final float n2) {
        if (!overflow) {
            data.add(new PointF(n1, n2));
            isDirty = true;
            if (data.size() > 500) {
                if (!overflow) {
                    Log.d("Cluster-analysis", "Cluster-analysis overflows (N>500)");
                }
                this.overflow = true;
            }
        }
    }

    public Cluster getCluster(final int n) {
        if (data.size() != 0 && n >= 0 && n < nClusters) {
            if (isDirty) {
                restartComputationTask();
                return null;
            }
            if (n < clusters.size()) {
                return clusters.get(n);
            }
        }
        return null;
    }

    public int getNClusters() {
        return nClusters;
    }

    public boolean overflow() {
        return overflow;
    }

    @Override
    public void registerOnAggregationResultListener(final OnAggregationResult onAggregationResult) {
        this.resultListeners.add(onAggregationResult);
    }

    private void reset() {
        data.clear();
        if (computeClustersTask != null) {
            computeClustersTask.cancel(true);
        }
        clusters.clear();
        isDirty = true;
        overflow = false;
    }

    public void unregisterOnAggregationResultListener(final OnAggregationResult onAggregationResult) {
        resultListeners.remove(onAggregationResult);
    }

    public void withdraw() {
        if (computeClustersTask != null) {
            computeClustersTask.cancel(true);
        }
    }

    private class ComputeClustersTask extends AsyncTask<ArrayList<PointF>, Integer, Void> {
        private double distance;

        private boolean isCloser(final double n, final Cluster cluster1, final Cluster cluster2) {
            final PointF cog1 = cluster1.getCenterOfGroup();
            final PointF cog2 = cluster2.getCenterOfGroup();
            final double n2 = cog1.x - cog2.x;
            if (n2 > n || -n2 > n) {
                return false;
            }
            final double n3 = cog1.y - cog2.y;
            if (n3 > n || -n3 > n) {
                return false;
            }
            distance = Math.sqrt(n2 * n2 + n3 * n3);
            return distance < n;
        }

        protected Void doInBackground(final ArrayList<PointF>... array) {
            final ArrayList<PointF> list = array[0];
            final int size = list.size();
            clusters.clear();
            for (PointF point : list) {
                clusters.add(new Cluster(point, size));
            }
            while (clusters.size() > nClusters) {
                Cluster cluster1 = null;
                Cluster cluster2 = null;
                double distance = Double.MAX_VALUE;
                for (int i = 0; i < clusters.size() - 1; i++) {
                    if (isCancelled()) {
                        return null;
                    }
                    for (int j = i + 1; j < clusters.size(); j++) {
                        if (isCloser(distance, clusters.get(i), clusters.get(j))) {
                            distance = this.distance;
                            cluster1 = clusters.get(i);
                            cluster2 = clusters.get(j);
                        }
                    }
                }
                for (PointF point : cluster2.points) {
                    cluster1.add(point);
                }
                clusters.remove(cluster2);
                publishProgress(clusters.size());
            }
            return null;
        }

        protected void onPostExecute(final Void void1) {
            super.onPostExecute(void1);
            isDirty = false;
            Stream.of(resultListeners)
                    .forEach(OnAggregationResult::onResult);
        }

        protected void onProgressUpdate(final Integer... array) {
            super.onProgressUpdate(array);
            final int intValue = array[0];
            for (OnAggregationResult resultListener : resultListeners) {
                resultListener.onProgressUpdate(intValue);
            }
        }
    }

}
