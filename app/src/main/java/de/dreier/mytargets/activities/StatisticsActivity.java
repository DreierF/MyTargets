package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.SortedSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.models.LinearSeries;
import de.dreier.mytargets.models.LinearSeries.LinearPoint;
import de.dreier.mytargets.utils.TargetOpenHelper;
import de.dreier.mytargets.views.ChartView;

public class StatisticsActivity extends ActionBarActivity {
    public static final String ROUND_ID = "round_id";
    private ChartView chartView;
    private long mRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRound = getIntent().getLongExtra(ROUND_ID, 0);

        chartView = (ChartView) findViewById(R.id.chart_view);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        LinearSeries series = generateRoundSeries();
        LinearSeries regression = generateLinearRegression(series);
        if (regression != null) {
            regression.setLineColor(0xFFAA66CC);
            regression.setLineWidth(1.3f * metrics.density);
            chartView.addSeries(regression);
        }

        series.setLineColor(0xFF33B5E5);
        series.setLineWidth(2 * metrics.density);
        chartView.addSeries(series);
    }

    private LinearSeries generateRoundSeries() {
        TargetOpenHelper db = new TargetOpenHelper(this);
        ArrayList<TargetOpenHelper.Passe> passes = db.getRoundPasses(mRound, -1);
        TargetOpenHelper.Round r = db.getRound(mRound);

        LinearSeries series = new LinearSeries();

        int x = 0;
        for (TargetOpenHelper.Passe p : passes) {
            for (int zone : p.zones)
                series.addPoint(new LinearSeries.LinearPoint(x++, (long) zone));
        }
        chartView.setRoundInfo(r);
        return series;
    }

    private LinearSeries generateLinearRegression(LinearSeries data) {
        SortedSet<LinearPoint> points = data.getPoints();
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        // first pass: read in data, compute xbar and ybar
        int n = 0;
        double sumx = 0.0, sumy = 0.0;
        for (LinearPoint p : points) {
            x[n] = p.getX();
            y[n] = p.getY();
            sumx += x[n];
            sumy += y[n];
            n++;
        }
        if (n < 1)
            return null;

        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        LinearSeries series = new LinearSeries();
        long x0 = data.getMinX();
        long y0 = (long) (beta1 * x0 + beta0);
        long x1 = data.getMaxX();
        long y1 = (long) (beta1 * x1 + beta0);
        series.addPoint(new LinearSeries.LinearPoint(x0, y0));
        series.addPoint(new LinearSeries.LinearPoint(x1, y1));
        return series;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
