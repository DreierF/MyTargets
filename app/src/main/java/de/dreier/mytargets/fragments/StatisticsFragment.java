/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.SortedSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.StatisticsDataSource;
import de.dreier.mytargets.models.LinearSeries;
import de.dreier.mytargets.models.LinearSeries.LinearPoint;

public class StatisticsFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final String ARG_TRAINING_ID = "training_id";
    public static final String ARG_ROUND_ID = "round_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_statistics, container, false);

        int pos = getArguments().getInt(ARG_POSITION, 0);
        long training = getArguments().getLong(ARG_TRAINING_ID, 0);

        LineChart chartView = (LineChart) rootView.findViewById(R.id.chart_view);

        LineData data;

        switch (pos) {
            case 0:
                data = new StatisticsDataSource(getContext()).getAllTrainings();
                chartView.setDescription("Shows for all passes the percentage of reached points");
                break;
            default:
                data = new StatisticsDataSource(getContext()).getAllRounds(training);
                chartView.setDescription("");
                break;
            //default:
            //    series = generateRoundSeries();
        }
        /*LineDataSet regression = generateLinearRegression(series);
        if (regression != null) {
            regression.setLineColor(0xFFAA66CC);
            regression.setLineWidth(1.3);
            data.addDataSet(regression);
        }*/

        chartView.getXAxis().setEnabled(false);
        chartView.getAxisRight().setEnabled(false);
        chartView.setData(data);
        chartView.getLegend().setEnabled(false);
        chartView.animateXY(2000, 2000);

        return rootView;
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
