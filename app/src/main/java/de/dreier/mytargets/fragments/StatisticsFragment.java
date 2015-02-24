package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.SortedSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.LinearSeries;
import de.dreier.mytargets.models.LinearSeries.LinearPoint;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.views.ChartView;

public class StatisticsFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final String ARG_TRAINING_ID = "training_id";
    public static final String ARG_ROUND_ID = "round_id";
    private ChartView chartView;
    private long mRound;
    private long mTraining;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_statistics, container, false);

        int pos = getArguments().getInt(ARG_POSITION, 0);
        mTraining = getArguments().getLong(ARG_TRAINING_ID, 0);
        mRound = getArguments().getLong(ARG_ROUND_ID, 0);

        chartView = (ChartView) rootView.findViewById(R.id.chart_view);

        LinearSeries series;

        switch (pos) {
            case 0:
                series = generateAllSeries();
                break;
            case 1:
                series = generateTrainingSeries();
                break;
            default:
                series = generateRoundSeries();
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        LinearSeries regression = generateLinearRegression(series);
        if (regression != null) {
            regression.setLineColor(0xFFAA66CC);
            regression.setLineWidth(1.3f * metrics.density);
            chartView.addSeries(regression);
        }

        series.setLineColor(0xFF33B5E5);
        series.setLineWidth(2 * metrics.density);
        chartView.addSeries(series);

        return rootView;
    }

    private LinearSeries generateAllSeries() {
        DatabaseManager db = new DatabaseManager(getActivity());
        ArrayList<Integer> list = db.getAllTrainings();

        LinearSeries series = new LinearSeries();

        int x = 0;
        for (Integer percent : list)
                series.addPoint(new LinearPoint(x++, (long) 100-percent));
        chartView.setRoundInfo(null);
        return series;
    }

    private LinearSeries generateTrainingSeries() {
        DatabaseManager db = new DatabaseManager(getActivity());
        ArrayList<Integer> list = db.getAllRounds(mTraining);

        LinearSeries series = new LinearSeries();

        int x = 0;
        for (Integer percent : list)
            series.addPoint(new LinearPoint(x++, (long) 100-percent));
        chartView.setRoundInfo(null);
        return series;
    }

    private LinearSeries generateRoundSeries() {
        DatabaseManager db = new DatabaseManager(getActivity());
        ArrayList<Shot[]> passes = db.getRoundPasses(mRound, -1);
        Round r = db.getRound(mRound);

        LinearSeries series = new LinearSeries();

        int x = 0;
        for (Shot[] passe : passes)
            for (Shot shot : passe)
                series.addPoint(new LinearPoint(x++, (long) shot.zone));
        chartView.setRoundInfo(r);
        return series;
    }

    private LinearSeries generateLinearRegression(LinearSeries data) {
        SortedSet<LinearPoint> points = data.getPoints();
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        // first pass: read in data, compute x bar and y bar
        int n = 0;
        double sum_x = 0.0, sum_y = 0.0;
        for (LinearPoint p : points) {
            x[n] = p.getX();
            y[n] = p.getY();
            sum_x += x[n];
            sum_y += y[n];
            n++;
        }
        if (n < 1)
            return null;

        double x_bar = sum_x / n;
        double y_bar = sum_y / n;

        // second pass: compute summary statistics
        double xx_bar = 0.0, xy_bar = 0.0;
        for (int i = 0; i < n; i++) {
            xx_bar += (x[i] - x_bar) * (x[i] - x_bar);
            xy_bar += (x[i] - x_bar) * (y[i] - y_bar);
        }
        double beta1 = xy_bar / xx_bar;
        double beta0 = y_bar - beta1 * x_bar;

        LinearSeries series = new LinearSeries();
        long x0 = data.getMinX();
        double y0 = beta1 * x0 + beta0;
        long x1 = data.getMaxX();
        double y1 = beta1 * x1 + beta0;
        series.addPoint(new LinearPoint(x0, y0));
        series.addPoint(new LinearPoint(x1, y1));
        return series;
    }
}
