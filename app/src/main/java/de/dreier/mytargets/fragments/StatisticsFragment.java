/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.DispersionPatternActivity;
import de.dreier.mytargets.databinding.FragmentStatisticsBinding;
import de.dreier.mytargets.databinding.ItemImageSimpleBinding;
import de.dreier.mytargets.managers.dao.ArrowStatisticDataSource;
import de.dreier.mytargets.managers.dao.DataSourceBase;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.SelectableZone;
import de.dreier.mytargets.shared.utils.Color;
import de.dreier.mytargets.utils.DataLoaderBase;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.RoundedTextDrawable;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ArrowStatistic>> {

    public static final String ARG_TARGET = "target";
    public static final String ARG_ROUND_IDS = "round_ids";

    private long[] roundIds;
    private List<Round> rounds;

    private ArrowStatisticDataSource arrowStatisticDataSource;

    private List<ArrowStatistic> data;
    private ArrowStatisticAdapter adapter;
    private FragmentStatisticsBinding binding;
    private Target target;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);

        target = Parcels.unwrap(getArguments().getParcelable(ARG_TARGET));
        roundIds = getArguments().getLongArray(ARG_ROUND_IDS);
        rounds = Stream.of(Utils.toList(roundIds))
                .map(id -> new RoundDataSource().get(id))
                .collect(Collectors.toList());
        ToolbarUtils.showHomeAsUp(this);

        LineData data = getLineChartDataSet();
        binding.chartView.getXAxis().setTextSize(20);
        binding.chartView.getXAxis().setTextColor(0xFF848484);
        binding.chartView.getXAxis().setEnabled(false);
        binding.chartView.getAxisRight().setEnabled(false);
        binding.chartView.setData(data);
        binding.chartView.getLegend().setEnabled(false);
        binding.chartView.animateXY(2000, 2000);

        binding.arrows.setHasFixedSize(true);

        //binding.dispersionView;
        binding.chartOverlay.setOnClickListener(v -> onChartClick());
        arrowStatisticDataSource = new ArrowStatisticDataSource();
        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        // configure pie chart
        binding.distributionChart.setUsePercentValues(true);

        // enable hole and configure
        binding.distributionChart.setDrawHoleEnabled(true);
        binding.distributionChart.setHoleRadius(7);
        binding.distributionChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        binding.distributionChart.setRotationAngle(0);
        binding.distributionChart.setRotationEnabled(true);
        addPieData();
        return binding.getRoot();
    }

    private void addPieData() {
        List<Map.Entry<SelectableZone, Integer>> scores = new PasseDataSource().getSortedScoreDistribution(rounds);

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (Map.Entry<SelectableZone, Integer> s : scores) {
            xValues.add(s.getKey().text);
            yValues.add(new Entry(s.getValue(), xValues.size() - 1));
            colors.add(s.getKey().zone.getFillColor());
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xValues, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        binding.distributionChart.setData(data);
        binding.distributionChart.highlightValues(null);
        binding.distributionChart.invalidate();
    }

    private void onChartClick() {
        //TODO animate chart to full size
    }

    @Override
    public Loader<List<ArrowStatistic>> onCreateLoader(int id, Bundle args) {
        arrowStatisticDataSource = new ArrowStatisticDataSource();
        return new DataLoaderBase<ArrowStatistic, DataSourceBase>(getContext(),
                arrowStatisticDataSource, arrowStatisticDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<ArrowStatistic>> loader, List<ArrowStatistic> data) {
        this.data = data;
        Collections.sort(data);
        if (binding.arrows.getAdapter() == null) {
            adapter = new ArrowStatisticAdapter();
            binding.arrows.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ArrowStatistic>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
    }

    @NonNull
    private LineData getLineChartDataSet() {
        List<Pair<Integer, Long>> values = Stream.of(Utils.toList(roundIds))
                .flatMap(roundId -> Stream.of(new PasseDataSource().getAllByRound(roundId)))
                .map(passe->getPairEndSummary(target, passe))
                .collect(Collectors.toList());

        final DateFormat dateFormat = DateFormat.getDateInstance();
        List<String> xValues = Stream.of(values)
                .map(v -> dateFormat.format(new DateTime(v.getSecond()).toDate()))
                .collect(Collectors.toList());
        LineData data = new LineData(xValues, generateLinearRegressionLine(values));
        data.setDrawValues(false);
        data.addDataSet(convertToLineData(values));
        return data;
    }

    private Pair<Integer, Long> getPairEndSummary(Target target, Passe passe) {
        int actCounter = 0;
        for(Shot s : passe.shot) {
            actCounter += target.getPointsByZone(s.zone, s.index);
        }
        return new Pair<>(actCounter, passe.saveDate.getMillis());
    }

    @NonNull
    private LineDataSet convertToLineData(List<Pair<Integer, Long>> values) {
        List<Entry> seriesEntries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            seriesEntries.add(new Entry(values.get(i).getSecond(), i));
        }

        LineDataSet series = new LineDataSet(seriesEntries, "");
        final int color = ApplicationInstance.getContext().getResources()
                .getColor(R.color.colorPrimary);
        series.setColors(new int[]{color});
        series.setLineWidth(2);
        series.setCircleColor(color);
        series.setCircleRadius(5);
        series.setCircleColorHole(color);
        return series;
    }

    private ILineDataSet generateLinearRegressionLine(List<Pair<Integer, Long>> values) {
        int dataSetSize = values.size();
        int[] x = new int[dataSetSize];
        float[] y = new float[dataSetSize];
        // first pass: read in data, compute x bar and y bar
        int n = 0;
        float sum_x = 0.0f, sum_y = 0.0f;
        for (int i = 0; i < dataSetSize; i++) {
            x[n] = values.get(i).getFirst();
            y[n] = values.get(i).getSecond();
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
        float y0 = beta1 * values.get(0).getFirst() + beta0;
        float y1 = beta1 * values.get(dataSetSize - 1).getFirst() + beta0;
        Entry first = new Entry(y0, 0);
        Entry last = new Entry(y1, dataSetSize - 1);
        List<Entry> yValues = Arrays.asList(first, last);
        LineDataSet lineDataSet = new LineDataSet(yValues, "");
        lineDataSet.setColors(new int[]{0xffdbdbdb});
        lineDataSet.setCircleRadius(0);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setLineWidth(1);
        return lineDataSet;
    }

    private class ArrowStatisticAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_simple, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindItem(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemImageSimpleBinding binding;
        private ArrowStatistic mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            binding = DataBindingUtil.bind(itemView);
            binding.content.setOnClickListener(v -> onItemClicked());
        }

        private void onItemClicked() {
            Intent i = new Intent(getContext(), DispersionPatternActivity.class);
            i.putExtra(DispersionPatternActivity.ITEM, Parcels.wrap(mItem));
            startActivity(i);
        }

        void bindItem(ArrowStatistic item) {
            mItem = item;
            binding.name.setText(
                    getString(R.string.arrow_x_of_set_of_arrows, item.arrowNumber, item.arrowName));
            binding.image.setImageDrawable(new RoundedTextDrawable(item));
        }
    }
}
