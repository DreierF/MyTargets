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

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.StatisticsDataSource;

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
                chartView.setDescription("Percentage of reached points (for all passes)");
                break;
            default:
                data = new StatisticsDataSource(getContext()).getAllRounds(training);
                chartView.setDescription("Percentage of reached points (for all passes of the current round)");
                break;
        }

        chartView.getXAxis().setEnabled(false);
        chartView.getAxisRight().setEnabled(false);
        chartView.setData(data);
        chartView.getLegend().setEnabled(false);
        chartView.animateXY(2000, 2000);

        return rootView;
    }
}
