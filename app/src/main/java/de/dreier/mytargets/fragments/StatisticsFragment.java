/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.StatisticsDataSource;
import de.dreier.mytargets.models.LinearSeries;
import de.dreier.mytargets.models.LinearSeries.LinearPoint;

public class StatisticsFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final String ARG_TRAINING_ID = "training_id";
    public static final String ARG_ROUND_ID = "round_id";
    private long mTraining;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_statistics, container, false);

        int pos = getArguments().getInt(ARG_POSITION, 0);
        mTraining = getArguments().getLong(ARG_TRAINING_ID, 0);

        LineChart chartView = (LineChart) rootView.findViewById(R.id.chart_view);

        LineDataSet series;

        switch (pos) {
            case 0:
                series = generateAllSeries();
                break;
            default:
                series = generateTrainingSeries();
                break;
            //default:
            //    series = generateRoundSeries();
        }
        LineData data = new LineData();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        /*LineDataSet regression = generateLinearRegression(series);
        if (regression != null) {
            regression.setLineColor(0xFFAA66CC);
            regression.setLineWidth(1.3f * metrics.density);
            data.addDataSet(regression);
        }*/

        series.setColors(new int[]{0xFF33B5E5});
        series.setLineWidth(2 * metrics.density);
        data.addDataSet(series);
        chartView.setData(data);

        return rootView;
    }

    private LineDataSet generateAllSeries() {
        List<Entry> list = new StatisticsDataSource(getContext()).getAllTrainings();
        return new LineDataSet(list, "All trainings");
    }

    private LineDataSet generateTrainingSeries() {
        ArrayList<Entry> list = new StatisticsDataSource(getContext()).getAllRounds(mTraining);
        return new LineDataSet(list, "Training");
    }

    /*private LineDataSet generateRoundSeries() {
        PasseDataSource passeDataSource = new PasseDataSource(getContext());
        ArrayList<Passe> passes = passeDataSource.getAll(mRound);
        RoundDataSource roundDataSource = new RoundDataSource(getContext());
        Round r = roundDataSource.get(mRound);

        LinearSeries series = new LinearSeries();

        int x = 0;
        for (Passe passe : passes) {
            for (Shot shot : passe.shot) {
                series.addPoint(new LinearPoint(x++, (long) shot.zone));
            }
        }
        //chartView.setRoundInfo(r);
        return series;
    }*/

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
        if (n < 1) {
            return null;
        }

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
