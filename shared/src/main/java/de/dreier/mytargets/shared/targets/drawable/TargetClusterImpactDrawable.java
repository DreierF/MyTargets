package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.ArrayList;

import de.dreier.mytargets.shared.analysis.aggregation.Cluster;
import de.dreier.mytargets.shared.analysis.aggregation.ClusterStrategy;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy;
import de.dreier.mytargets.shared.models.Target;

public class TargetClusterImpactDrawable extends TargetImpactDrawable implements IAggregationStrategy.OnAggregationResult {

    private static final int N_CLUSTERS = 4;
    private static final String TAG = "TargetClusterImpactDraw";
    private final Path[] clusterPaths;
    private final Path[] drawClusterPaths;
    private final Paint[] innerPaints;
    private ArrayList<ClusterStrategy> faceAggregations;

    public TargetClusterImpactDrawable(Target target) {
        super(target);
        faceAggregations = new ArrayList<>();
        for (int i = 0; i < model.getFaceCount(); i++) {
            final ClusterStrategy clusterStrategy = new ClusterStrategy(N_CLUSTERS);
            clusterStrategy.registerOnAggregationResultListener(this);
            clusterStrategy.getNClusters();
            faceAggregations.add(clusterStrategy);
        }
        this.clusterPaths = new Path[N_CLUSTERS];
        this.drawClusterPaths = new Path[N_CLUSTERS];
        this.innerPaints = new Paint[N_CLUSTERS];

        for (int i = 0; i < N_CLUSTERS; ++i) {
            this.clusterPaths[i] = new Path();
            this.drawClusterPaths[i] = new Path();
            this.innerPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.innerPaints[i].setStrokeWidth(0.1F);
            this.innerPaints[i].setARGB(255, 255, 0, 0);
            this.innerPaints[i].setStyle(Paint.Style.FILL);
        }

        this.clear();
        setColor(0xAAAAAAAA);
    }

    public void clear() {
        for (ClusterStrategy strategy : faceAggregations) {
            for (int i = 0; i < strategy.getNClusters(); ++i) {
                this.clusterPaths[i].rewind();
            }
        }
    }

    @Override
    public void onResult() {
        Log.d(TAG, "onResult: ");
        invalidateSelf();
    }

    @Override
    public void onProgressUpdate(int paramInt) {

    }

    @Override
    protected void onPostDraw(Canvas canvas, int faceIndex) {
        super.onPostDraw(canvas, faceIndex);
        final ClusterStrategy clusterStrategy = faceAggregations.get(faceIndex);
        for (int i = 0; i < clusterStrategy.getNClusters(); ++i) {
            Log.i(TAG, "onPostDraw: Face: " + faceIndex + " cluster: " + i);
            this.onPrepareDraw(clusterStrategy, i);
            this.onDraw(canvas, i);
        }
    }

    private void onPrepareDraw(ClusterStrategy clusterStrategy, int index) {
        Cluster cluster = clusterStrategy.getCluster(index);
        if (cluster != null) {
            Log.i(TAG, "onPrepareDraw: " + cluster.toString());
            float v = (float) (Math.sqrt(cluster.getWeight()) * 2.0D);
            PointF center = cluster.getCenterOfGroup();
            this.clusterPaths[index].rewind();
            this.clusterPaths[index]
                    .addOval(new RectF(center.x - v, center.y - v, center.x + v, center.y + v),
                            Path.Direction.CW);
        }
    }

    private void onDraw(Canvas canvas, int index) {
        this.drawClusterPaths[index].set(this.clusterPaths[index]);
        canvas.drawPath(this.drawClusterPaths[index], this.innerPaints[index]);
    }

    public void onWithDrawDrawable() {
        for (ClusterStrategy cluster : faceAggregations) {
            cluster.unregisterOnAggregationResultListener(this);
            cluster.withdraw();
        }
    }

    public void setColor(@ColorInt int color) {
        for (int i = 0; i < N_CLUSTERS; ++i) {
            this.innerPaints[i].setColor(color);
        }
    }

    @Override
    public void notifyArrowSetChanged() {
        super.notifyArrowSetChanged();
        recalculateAggregation();
    }

    private void recalculateAggregation() {
        for (int i = 0; i < model.getFaceCount(); i++) {
            faceAggregations.get(i).calculate(shots.get(i));
        }
    }
}
