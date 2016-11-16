/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.fragments;

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
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.utils.Color;
import de.dreier.mytargets.utils.DataLoaderBase;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.RoundedTextDrawable;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ArrowStatistic>> {

    private static final String ARG_TARGET = "target";
    private static final String ARG_ROUND_IDS = "round_ids";
    private static final String ARG_ANIMATE = "animate";
    private static final String PIE_CHART_CENTER_TEXT_FORMAT = "<font color='gray'>%s</font><br>" +
            "<big>%s</big><br>" +
            "<small>&nbsp;</small><br>" +
            "<font color='gray'>%s</font><br>" +
            "<big>%d</big>";

    private long[] roundIds;
    private List<Round> rounds;
    private ArrowStatisticDataSource arrowStatisticDataSource;
    private ArrowStatisticAdapter adapter;
    private FragmentStatisticsBinding binding;
    private Target target;
    private boolean animate;

    public static StatisticsFragment newInstance(List<Long> roundIds, Target item, boolean animate) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StatisticsFragment.ARG_TARGET, Parcels.wrap(item));
        bundle.putLongArray(StatisticsFragment.ARG_ROUND_IDS, Utils.toArray(roundIds));
        bundle.putBoolean(StatisticsFragment.ARG_ANIMATE, animate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);

        target = Parcels.unwrap(getArguments().getParcelable(ARG_TARGET));
        roundIds = getArguments().getLongArray(ARG_ROUND_IDS);
        animate = getArguments().getBoolean(ARG_ANIMATE);
        rounds = Stream.of(Utils.toList(roundIds))
                .map(id -> new RoundDataSource().get(id))
                .collect(Collectors.toList());

        showLineChart();
        showPieChart();
        showDispersionView();

        binding.arrows.setHasFixedSize(true);
        adapter = new ArrowStatisticAdapter();
        binding.arrows.setAdapter(adapter);
        binding.arrows.setNestedScrollingEnabled(false);

        ToolbarUtils.showHomeAsUp(this);
        return binding.getRoot();
    }

    private void showDispersionView() {
        final List<Shot> exactShots = Stream.of(rounds)
                .flatMap(r -> Stream.of(new PasseDataSource().getAllByRound(r.getId())))
                .filter(p -> p.exact)
                .flatMap(p -> Stream.of(p.shots))
                .collect(Collectors.toList());
        if (exactShots.isEmpty()) {
            binding.dispersionPatternLayout.setVisibility(View.GONE);
            return;
        }
        binding.dispersionView.setTarget(target.getImpactAggregationDrawable());
        binding.dispersionView.setShoots(exactShots);
        binding.dispersionView.setEnabled(false);
        binding.dispersionViewOverlay.setOnClickListener(view -> {
            ArrowStatistic statistics = new ArrowStatistic();
            statistics.target = target;
            statistics.addShots(exactShots);
            DispersionPatternActivity
                    .getIntent(this, statistics)
                    .start();
        });
    }

    private void showLineChart() {
        LineData data = getLineChartDataSet();
        if (data == null) {
            return;
        }
        binding.chartView.getXAxis().setTextSize(10);
        binding.chartView.getXAxis().setTextColor(0xFF848484);
        binding.chartView.getAxisRight().setEnabled(false);
        binding.chartView.getLegend().setEnabled(false);
        binding.chartView.setData(data);
        binding.chartView.setDescription("");
        binding.chartView.getAxisLeft().setAxisMinValue(0);
        binding.chartView.getXAxis().setDrawGridLines(false);
        binding.chartView.setDoubleTapToZoomEnabled(false);
        if(animate) {
            binding.chartView.animateXY(2000, 2000);
        }
    }

    private void showPieChart() {
        // enable hole and configure
        binding.distributionChart.setTransparentCircleRadius(15);
        binding.distributionChart.setHoleColor(0x00EEEEEE);
        binding.distributionChart.getLegend().setEnabled(false);
        binding.distributionChart.setDescription("");

        // enable rotation of the chart by touch
        binding.distributionChart.setRotationAngle(0);
        binding.distributionChart.setRotationEnabled(true);

        binding.distributionChart.setUsePercentValues(false);
        binding.distributionChart.highlightValues(null);
        binding.distributionChart.setBackgroundColor(0x00EEEEEE);
        binding.distributionChart.invalidate();
        addPieData();
    }

    private void addPieData() {
        List<Map.Entry<SelectableZone, Integer>> scores = new PasseDataSource()
                .getSortedScoreDistribution(rounds);

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Integer> textColors = new ArrayList<>();

        for (Map.Entry<SelectableZone, Integer> s : scores) {
            if (s.getValue() > 0) {
                xValues.add(s.getKey().text);
                yValues.add(new Entry(s.getValue(), xValues.size() - 1));
                colors.add(s.getKey().zone.getFillColor());
                textColors.add(s.getKey().zone.getTextColor());
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xValues, dataSet);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.GRAY);
        data.setDrawValues(false);
        data.setValueTextColors(textColors);

        binding.distributionChart.setData(data);
        final String text = getHitMissText();
        binding.distributionChart.setCenterText(HtmlUtils.fromHtml(text));

        binding.distributionChart
                .setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                        final String s = String.format(Locale.ENGLISH,
                                PIE_CHART_CENTER_TEXT_FORMAT,
                                getString(R.string.points), xValues.get(e.getXIndex()),
                                getString(R.string.count), (int) e.getVal());
                        binding.distributionChart.setCenterText(HtmlUtils.fromHtml(s));
                    }

                    @Override
                    public void onNothingSelected() {
                        binding.distributionChart.setCenterText(HtmlUtils.fromHtml(text));
                    }
                });
    }

    private String getHitMissText() {
        final List<Shot> shots = Stream.of(rounds)
                .flatMap(r -> Stream.of(new PasseDataSource().getAllByRound(r.getId())))
                .flatMap(p -> Stream.of(p.shots))
                .collect(Collectors.toList());
        long missCount = Stream.of(shots).filter(s -> s.zone == Shot.MISS).count();
        long hitCount = shots.size() - missCount;

        return String.format(Locale.ENGLISH,
                PIE_CHART_CENTER_TEXT_FORMAT,
                getString(R.string.hits), String.valueOf(hitCount),
                getString(R.string.misses), missCount);
    }

    @Override
    public Loader<List<ArrowStatistic>> onCreateLoader(int id, Bundle args) {
        arrowStatisticDataSource = new ArrowStatisticDataSource();
        return new DataLoaderBase<ArrowStatistic, DataSourceBase>(getContext(),
                arrowStatisticDataSource,
                () -> arrowStatisticDataSource.getAll(Utils.toList(roundIds)));
    }

    @Override
    public void onLoadFinished(Loader<List<ArrowStatistic>> loader, List<ArrowStatistic> data) {
        binding.arrowRankingLabel.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
        Collections.sort(data);
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ArrowStatistic>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    private LineData getLineChartDataSet() {
        List<Pair<Integer, DateTime>> values = Stream.of(Utils.toList(roundIds))
                .flatMap(roundId -> Stream.of(new PasseDataSource().getAllByRound(roundId)))
                .map(passe -> getPairEndSummary(target, passe))
                .collect(Collectors.toList());
        if (values.isEmpty()) {
            return null;
        }

        final DateFormat dateFormat = getDateFormat(values);
        List<String> xValues = Stream.of(values)
                .map(v -> dateFormat.format(v.getSecond().toDate()))
                .collect(Collectors.toList());

        LineData data;
        if (values.size() < 2) {
            // Without regression line
            data = new LineData(xValues, convertToLineData(values));
        } else {
            data = new LineData(xValues, generateLinearRegressionLine(values));
            data.addDataSet(convertToLineData(values));
        }
        data.setDrawValues(false);
        return data;
    }

    private DateFormat getDateFormat(List<Pair<Integer, DateTime>> values) {
        Optional<DateTime> firstDate = Stream.of(values).map(Pair::getSecond)
                .min(DateTimeComparator.getDateOnlyInstance());
        Optional<DateTime> lastDate = Stream.of(values).map(Pair::getSecond)
                .max(DateTimeComparator.getDateOnlyInstance());

        if (firstDate.equals(lastDate)) {
            return DateFormat.getTimeInstance(DateFormat.SHORT);
        } else {
            return DateFormat.getDateInstance();
        }
    }

    private Pair<Integer, DateTime> getPairEndSummary(Target target, Passe passe) {
        int actCounter = 0;
        for (Shot s : passe.shots) {
            actCounter += target.getPointsByZone(s.zone, s.index);
        }
        return new Pair<>(actCounter, passe.saveDate);
    }

    @NonNull
    private LineDataSet convertToLineData(List<Pair<Integer, DateTime>> values) {
        List<Entry> seriesEntries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            seriesEntries.add(new Entry(values.get(i).getFirst(), i));
        }

        LineDataSet series = new LineDataSet(seriesEntries, "");
        final int color = ApplicationInstance.getContext().getResources()
                .getColor(R.color.colorPrimary);
        series.setColors(new int[]{color});
        series.setLineWidth(2);
        series.setCircleColor(color);
        series.setCircleRadius(5);
        series.setCircleColorHole(color);
        series.setDrawValues(false);
        series.setHighLightColor(0xff9c9c9c);
        series.setDrawHorizontalHighlightIndicator(false);
        series.setDrawVerticalHighlightIndicator(true);
        series.enableDashedHighlightLine(4, 4, 0);
        return series;
    }

    private ILineDataSet generateLinearRegressionLine(List<Pair<Integer, DateTime>> values) {
        int dataSetSize = values.size();
        double[] x = new double[dataSetSize];
        double[] y = new double[dataSetSize];
        // first pass: read in data, compute x bar and y bar
        int n = 0;
        double sumX = 0.0f;
        double sumY = 0.0f;
        for (int i = 0; i < dataSetSize; i++) {
            x[n] = values.get(i).getSecond().getMillis();
            y[n] = values.get(i).getFirst();
            sumX += x[n];
            sumY += y[n];
            n++;
        }
        if (n < 1) {
            return null;
        }
        double xBar = sumX / n;
        double yBar = sumY / n;

        // second pass: compute summary statistics
        double xxBar = 0.0f;
        double xyBar = 0.0f;
        for (int i = 0; i < n; i++) {
            xxBar += (x[i] - xBar) * (x[i] - xBar);
            xyBar += (x[i] - xBar) * (y[i] - yBar);
        }
        double beta1 = xyBar / xxBar;
        double beta0 = yBar - beta1 * xBar;
        float y0 = (float) (beta1 * values.get(0).getSecond().getMillis() + beta0);
        float y1 = (float) (beta1 * values.get(dataSetSize - 1).getSecond().getMillis() + beta0);
        Entry first = new Entry(y0, 0);
        Entry last = new Entry(y1, dataSetSize - 1);
        List<Entry> yValues = Arrays.asList(first, last);
        LineDataSet lineDataSet = new LineDataSet(yValues, "");
        lineDataSet.setColors(new int[]{0xffff9100});
        lineDataSet.setCircleRadius(0);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setLineWidth(1);
        return lineDataSet;
    }

    private class ArrowStatisticAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<ArrowStatistic> data = new ArrayList<>();

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

        public void setData(List<ArrowStatistic> data) {
            this.data = data;
            notifyDataSetChanged();
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
            DispersionPatternActivity.getIntent(StatisticsFragment.this, mItem).start();
        }

        void bindItem(ArrowStatistic item) {
            mItem = item;
            binding.name.setText(
                    getString(R.string.arrow_x_of_set_of_arrows, item.arrowNumber, item.arrowName));
            binding.image.setImageDrawable(new RoundedTextDrawable(item));
        }
    }
}
