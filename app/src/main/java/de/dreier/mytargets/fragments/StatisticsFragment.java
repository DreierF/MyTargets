/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentStatisticsBinding;
import de.dreier.mytargets.managers.dao.StatisticsDataSource;

public class StatisticsFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final String ARG_TRAINING_ID = "training_id";
    public static final String ARG_ROUND_ID = "round_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentStatisticsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);

        int pos = getArguments().getInt(ARG_POSITION, 0);
        long training = getArguments().getLong(ARG_TRAINING_ID, 0);

        LineData data;

        switch (pos) {
            case 0:
                data = new StatisticsDataSource().getAllTrainings();
                binding.chartView.setDescription(getString(R.string.percentage_reached_points));
                break;
            default:
                data = new StatisticsDataSource().getAllRounds(training);
                binding.chartView.setDescription(getString(R.string.percentage_reached_points));
                break;
        }

        List<String> xs = data.getXVals();
        ILineDataSet ys = data.getDataSets().get(0);
        ILineDataSet regressionLine = generateLinearRegressionLine(xs, ys);
        data.addDataSet(regressionLine);

        binding.chartView.getXAxis().setEnabled(false);
        binding.chartView.getAxisRight().setEnabled(false);
        binding.chartView.setData(data);
        binding.chartView.getLegend().setEnabled(false);
        binding.chartView.animateXY(2000, 2000);

        return binding.getRoot();
    }

    //TODO refactor to get rid of the Integer.parseInt() calls
    private ILineDataSet generateLinearRegressionLine(List<String> xs, ILineDataSet dataSet) {
        final int dataSetSize = dataSet.getEntryCount();
        int[] x = new int[dataSetSize];
        float[] y = new float[dataSetSize];
        // first pass: read in data, compute x bar and y bar
        int n = 0;
        float sum_x = 0.0f, sum_y = 0.0f;
        for (int i = 0; i < dataSetSize; i++) {
            final Entry entry = dataSet.getEntryForIndex(i);
            final String xVal = xs.get(entry.getXIndex());
            x[n] = Integer.parseInt(xVal);
            y[n] = entry.getVal();
            sum_x += x[n];
            sum_y += y[n];
            n++;
        }
        if (n < 1) {
            return null;
        }
        float x_bar = sum_x / n;
        float y_bar = sum_y / n;

        // second pass: compute summary statistics
        float xx_bar = 0.0f, xy_bar = 0.0f;
        for (int i = 0; i < n; i++) {
            xx_bar += (x[i] - x_bar) * (x[i] - x_bar);
            xy_bar += (x[i] - x_bar) * (y[i] - y_bar);
        }
        float beta1 = xy_bar / xx_bar;
        float beta0 = y_bar - beta1 * x_bar;
        float y0 = beta1 * Integer.parseInt(xs.get(0)) + beta0;
        float y1 = beta1 * Integer.parseInt(xs.get(dataSetSize - 1)) + beta0;
        Entry first = new Entry(y0, 0);
        Entry last = new Entry(y1, dataSetSize - 1);
        List<Entry> yValues = Arrays.asList(first, last);
        LineDataSet lineDataSet = new LineDataSet(yValues, "");
        lineDataSet.setColors(new int[]{0xFF888888});
        lineDataSet.setCircleRadius(0);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setLineWidth(2);
        return lineDataSet;
    }
}
