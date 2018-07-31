/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.statistics

import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.fragments.FragmentBase
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.databinding.FragmentStatisticsBinding
import de.dreier.mytargets.databinding.ItemImageSimpleBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.utils.Color
import de.dreier.mytargets.utils.*
import de.dreier.mytargets.utils.MobileWearableClient.Companion.BROADCAST_UPDATE_TRAINING_FROM_REMOTE
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

class StatisticsFragment : FragmentBase() {

    private var roundIds: LongArray? = null
    private var rounds: List<Round>? = null
    private var adapter: ArrowStatisticAdapter? = null
    private lateinit var binding: FragmentStatisticsBinding
    private var target: Target? = null
    private var animate: Boolean = false

    private val database = ApplicationInstance.db
    private val trainingDAO = database.trainingDAO()
    private val roundDAO = database.roundDAO()
    private val endDAO = database.endDAO()

    private val updateReceiver = object : MobileWearableClient.EndUpdateReceiver() {

        override fun onUpdate(trainingId: Long, roundId: Long, end: End) {
            if (roundIds!!.contains(roundId)) {
                reloadData()
            }
        }
    }

    private val hitMissText: String
        get() {
            val shots = rounds!!
                .flatMap { r -> roundDAO.loadEnds(r.id) }
                .flatMap { endDAO.loadShots(it.id) }
                .filter { (_, _, _, _, _, scoringRing) -> scoringRing != Shot.NOTHING_SELECTED }
            val missCount =
                shots.filter { (_, _, _, _, _, scoringRing) -> scoringRing == Shot.MISS }.count()
                    .toLong()
            val hitCount = shots.size - missCount

            return String.format(
                Locale.US, PIE_CHART_CENTER_TEXT_FORMAT,
                getString(R.string.hits), hitCount.toString(),
                getString(R.string.misses), missCount
            )
        }

    private// Without regression line
    val lineChartDataSet: LineData?
        get() {
            val isSingleTraining = rounds!!.distinctBy(Round::trainingId).count() == 1

            val values = getDataPointsForLineChart(isSingleTraining)
            if (values?.isEmpty() != false) {
                return null
            }

            val eval = getEntryEvaluator(values, isSingleTraining)
            binding.chartView.xAxis.setValueFormatter { value, _ -> eval.getXValueFormatted(value) }

            val data: LineData
            if (values.size < 2) {
                data = LineData(convertToLineData(values, eval))
            } else {
                data = LineData(generateLinearRegressionLine(values, eval))
                data.addDataSet(convertToLineData(values, eval))
            }
            data.setDrawValues(false)
            return data
        }

    private fun getDataPointsForLineChart(isSingleTraining: Boolean): List<Pair<Float, LocalDateTime>>? {
        val trainingsMap = rounds!!
            .map { it.trainingId!! }
            .distinct()
            .map { trainingDAO.loadTraining(it) }
            .map { Pair(it.id, it) }
            .toMap()

        val values: List<Pair<Float, LocalDateTime>>
        if (isSingleTraining) {
            val trainingDate = trainingsMap.values.toList()[0].date
            val ends = rounds!!.sortedBy { it.index }.map { roundDAO.loadEnds(it.id) }
            val firstRound = ends[0]
            if (firstRound.isEmpty()) {
                return null
            }
            var firstEndTime = firstRound[0].saveTime
            var dayShift = 0L
            values = ends.flatMap { it }
                .map { end ->
                    if (end.saveTime!!.isBefore(firstEndTime)) {
                        dayShift += 1
                        firstEndTime = end.saveTime
                    }
                    val dateTime = LocalDateTime.of(trainingDate, end.saveTime).plusDays(dayShift)
                    Pair(end.score.shotAverage, dateTime)
                }
                .sortedBy { (_, date) -> date }
        } else {
            values = rounds!!
                .map { r -> Pair(trainingsMap[r.trainingId]!!.date, r) }
                .flatMap { (date, round) ->
                    roundDAO.loadEnds(round.id).map { end -> Pair(end, date) }
                }
                .map { (end, date) ->
                    Pair(
                        end.score.shotAverage,
                        LocalDateTime.of(date, end.saveTime!!)
                    )
                }
                .sortedBy { (_, date) -> date }
        }
        return values
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            updateReceiver,
            IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(updateReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)

        val target = arguments!!.getParcelable<Target>(ARG_TARGET)
        if (SettingsManager.statisticsDispersionPatternMergeSpot) {
            this.target = Target.singleSpotTargetFrom(target!!)
        } else {
            this.target = target
        }
        roundIds = arguments!!.getLongArray(ARG_ROUND_IDS)
        animate = arguments!!.getBoolean(ARG_ANIMATE)

        binding.arrows.setHasFixedSize(true)
        adapter = ArrowStatisticAdapter()
        binding.arrows.adapter = adapter
        binding.arrows.isNestedScrollingEnabled = false

        ToolbarUtils.showHomeAsUp(this)
        return binding.root
    }

    override fun onLoad(args: Bundle?): LoaderUICallback {
        rounds = roundDAO.loadRounds(roundIds!!)
        val data = ArrowStatistic.getAll(target!!, rounds!!)
            .sortedWith(compareByDescending { it.totalScore.shotAverage })

        return {
            showLineChart()
            showPieChart()
            showDispersionView()
            binding.distributionChart.invalidate()
            binding.chartView.invalidate()

            binding.arrowRankingLabel.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
            adapter!!.setData(data)
        }
    }

    private fun showDispersionView() {
        val exactShots = rounds!!
            .flatMap { roundDAO.loadEnds(it.id) }
            .filter { it.exact }
            .flatMap { endDAO.loadShots(it.id) }
            .filter { (_, _, _, _, _, scoringRing) -> scoringRing != Shot.NOTHING_SELECTED }
            .toList()
        if (exactShots.isEmpty()) {
            binding.dispersionPatternLayout.visibility = View.GONE
            return
        }
        val stats = ArrowStatistic(target!!, exactShots)
        stats.arrowDiameter = Dimension(5f, Dimension.Unit.MILLIMETER)

        val trainingsIds = rounds!!
            .map { it.trainingId!! }
            .distinct()
        if (trainingsIds.size == 1) {
            val training = trainingDAO.loadTraining(trainingsIds[0])
            val date = training.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val round = if (rounds!!.size == 1) {
                val index = rounds!![0].index + 1
                "-" + resources.getQuantityString(R.plurals.rounds, index, index)
                    .replace(' ', '-')
            } else ""
            stats.exportFileName = "$date-${training.title}$round"
        } else {
            stats.exportFileName = ""
        }

        val drawable = DispersionPatternUtils.targetFromArrowStatistics(stats)
        binding.dispersionView.setImageDrawable(drawable)

        binding.dispersionViewOverlay.setOnClickListener {
            navigationController.navigateToDispersionPattern(stats)
        }
    }

    private fun showLineChart() {
        val data = lineChartDataSet ?: return
        binding.chartView.xAxis.textSize = 10f
        binding.chartView.xAxis.textColor = -0x7b7b7c
        binding.chartView.axisRight.isEnabled = false
        binding.chartView.legend.isEnabled = false
        binding.chartView.data = data
        val desc = Description()
        desc.text = getString(R.string.average_arrow_score_per_end)
        binding.chartView.description = desc
        val maxCeil = Math.ceil(data.yMax.toDouble()).toInt()
        binding.chartView.axisLeft.axisMaximum = maxCeil.toFloat()
        binding.chartView.axisLeft.labelCount = maxCeil
        binding.chartView.axisLeft.axisMinimum = 0f
        binding.chartView.xAxis.setDrawGridLines(false)
        binding.chartView.isDoubleTapToZoomEnabled = false
        if (animate) {
            binding.chartView.animateXY(2000, 2000)
        }
        binding.chartView.renderer = object : LineChartRenderer(
            binding.chartView, binding.chartView.animator,
            binding.chartView.viewPortHandler
        ) {
            override fun drawHighlighted(canvas: Canvas, indices: Array<Highlight>) {
                mRenderPaint.style = Paint.Style.FILL

                val dataSets = mChart.lineData.dataSets

                var colorIndex = 0
                for (highlight in indices) {
                    val i = highlight.dataSetIndex
                    val dataSet = dataSets[i]

                    mRenderPaint.color = dataSet.getCircleColor(colorIndex)

                    val circleRadius = dataSet.circleRadius

                    canvas.drawCircle(
                        highlight.drawX,
                        highlight.drawY,
                        circleRadius,
                        mRenderPaint
                    )
                    colorIndex = (colorIndex + 1) % dataSet.circleColorCount
                }

                // draws highlight lines (if enabled)
                super.drawHighlighted(canvas, indices)
            }
        }
    }

    private fun showPieChart() {
        // enable hole and configure
        binding.distributionChart.transparentCircleRadius = 15f
        binding.distributionChart.setHoleColor(
            ContextCompat.getColor(
                context!!,
                R.color.md_grey_50
            )
        )
        binding.distributionChart.legend.isEnabled = false
        binding.distributionChart.description = EMPTY_DESCRIPTION

        // enable rotation of the chart by touch
        binding.distributionChart.rotationAngle = 0f
        binding.distributionChart.isRotationEnabled = true

        binding.distributionChart.setUsePercentValues(false)
        binding.distributionChart.highlightValues(null)
        binding.distributionChart.setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                R.color.md_grey_50
            )
        )
        binding.distributionChart.invalidate()
        addPieData()
    }

    private fun addPieData() {
        val scores = ScoreUtils.getSortedScoreDistribution(roundDAO, endDAO, rounds!!)

        val yValues = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        val textColors = ArrayList<Int>()

        for ((key, value) in scores) {
            if (value > 0) {
                yValues.add(PieEntry(value.toFloat(), key))
                colors.add(key.zone.fillColor)
                textColors.add(key.zone.textColor)
            }
        }

        // create pie data set
        val dataSet = PieDataSet(yValues, "")
        dataSet.setValueFormatter { _, entry, _, _ -> (entry.data as SelectableZone).text }
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors

        // instantiate pie data object now
        val data = PieData(dataSet)
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.GRAY)
        data.setDrawValues(true)
        data.setValueTextColors(textColors)

        binding.distributionChart.data = data
        val hitMissText = hitMissText
        binding.distributionChart.centerText = Utils.fromHtml(hitMissText)

        binding.distributionChart
            .setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {
                    val selectableZone = e.data as SelectableZone
                    val s = String.format(
                        Locale.US,
                        PIE_CHART_CENTER_TEXT_FORMAT,
                        getString(R.string.points), selectableZone.text,
                        getString(R.string.count), e.y.toInt()
                    )
                    binding.distributionChart.centerText = Utils.fromHtml(s)
                }

                override fun onNothingSelected() {
                    binding.distributionChart.centerText = Utils.fromHtml(hitMissText)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    private fun getEntryEvaluator(
        values: List<Pair<Float, LocalDateTime>>,
        singleTraining: Boolean
    ): Evaluator {

        val eval: Evaluator
        if (singleTraining) {
            eval = object : Evaluator {
                private val dateFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

                override fun getXValue(values: List<Pair<Float, LocalDateTime>>, i: Int): Long {
                    return Duration.between(values[0].second, values[i].second).seconds
                }

                override fun getXValueFormatted(value: Float): String {
                    val diffToFirst = value.toLong()
                    return values[0].second.plusSeconds(diffToFirst).format(dateFormat)
                }
            }
        } else {
            eval = object : Evaluator {
                private val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

                override fun getXValue(values: List<Pair<Float, LocalDateTime>>, i: Int): Long {
                    return i.toLong()
                }

                override fun getXValueFormatted(value: Float): String {
                    val index = Math.max(Math.min(value.toInt(), values.size - 1), 0)
                    return dateFormat.format(values[index].second)
                }
            }
        }
        return eval
    }

    private fun convertToLineData(
        values: List<Pair<Float, LocalDateTime>>,
        evaluator: Evaluator
    ): LineDataSet {
        val seriesEntries = values.indices.map {
            Entry(
                evaluator.getXValue(values, it).toFloat(),
                values[it].first
            )
        }

        val series = LineDataSet(seriesEntries, "")
        val color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        series.setColors(color)
        series.lineWidth = 2f
        series.setCircleColor(color)
        series.circleRadius = 5f
        series.setCircleColorHole(color)
        series.setDrawValues(false)
        series.setDrawCircles(false)
        series.highLightColor = -0x636364
        series.setDrawHorizontalHighlightIndicator(false)
        series.setDrawVerticalHighlightIndicator(true)
        series.enableDashedHighlightLine(4f, 4f, 0f)
        return series
    }

    /**
     * @param values Must not be empty
     */
    private fun generateLinearRegressionLine(
        values: List<Pair<Float, LocalDateTime>>,
        eval: Evaluator
    ): ILineDataSet {
        val dataSetSize = values.size
        val x = DoubleArray(dataSetSize)
        val y = DoubleArray(dataSetSize)
        // first pass: read in data, compute x bar and y bar
        var n = 0
        var sumX = 0.0
        var sumY = 0.0
        var minX = Long.MAX_VALUE
        var maxX = Long.MIN_VALUE
        for (i in 0 until dataSetSize) {
            x[n] = eval.getXValue(values, i).toDouble()
            y[n] = values[i].first.toDouble()
            sumX += x[n]
            sumY += y[n]
            if (x[n] < minX) {
                minX = x[n].toLong()
            }
            if (x[n] > maxX) {
                maxX = x[n].toLong()
            }
            n++
        }

        val xBar = sumX / n
        val yBar = sumY / n

        // second pass: compute summary statistics
        var xxBar = 0.0
        var xyBar = 0.0
        for (i in 0 until n) {
            xxBar += (x[i] - xBar) * (x[i] - xBar)
            xyBar += (x[i] - xBar) * (y[i] - yBar)
        }
        val beta1 = xyBar / xxBar
        val beta0 = yBar - beta1 * xBar
        val y0 = (beta1 * eval.getXValue(values, 0) + beta0).toFloat()
        val y1 = (beta1 * eval.getXValue(values, dataSetSize - 1) + beta0).toFloat()
        val first = Entry(minX.toFloat(), y0)
        val last = Entry(maxX.toFloat(), y1)
        val yValues = Arrays.asList(first, last)
        val lineDataSet = LineDataSet(yValues, "")
        lineDataSet.setColors(-0x6f00)
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.lineWidth = 1f
        lineDataSet.isHighlightEnabled = false
        return lineDataSet
    }

    private interface Evaluator {
        fun getXValue(values: List<Pair<Float, LocalDateTime>>, i: Int): Long

        fun getXValueFormatted(value: Float): String
    }

    private inner class ArrowStatisticAdapter : RecyclerView.Adapter<ViewHolder>() {

        private var data: List<ArrowStatistic> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_simple, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun setData(data: List<ArrowStatistic>) {
            this.data = data
            notifyDataSetChanged()
        }
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding: ItemImageSimpleBinding
        private var mItem: ArrowStatistic? = null

        init {
            itemView.isClickable = true
            binding = ItemImageSimpleBinding.bind(itemView)
            binding.content.setOnClickListener { onItemClicked() }
        }

        private fun onItemClicked() {
            navigationController.navigateToDispersionPattern(mItem!!)
        }

        fun bindItem(item: ArrowStatistic) {
            mItem = item
            binding.name.text = getString(
                R.string.arrow_x_of_set_of_arrows, item.arrowNumber, item
                    .arrowName
            )
            binding.image.setImageDrawable(RoundedTextDrawable(item))
        }
    }

    companion object {
        private const val ARG_TARGET = "target"
        private const val ARG_ROUND_IDS = "round_ids"
        private const val ARG_ANIMATE = "animate"
        private const val PIE_CHART_CENTER_TEXT_FORMAT = "<font color='gray'>%s</font><br>" +
                "<big>%s</big><br>" +
                "<small>&nbsp;</small><br>" +
                "<font color='gray'>%s</font><br>" +
                "<big>%d</big>"
        private val EMPTY_DESCRIPTION = Description()

        init {
            EMPTY_DESCRIPTION.text = ""
        }

        fun newInstance(roundIds: List<Long>, item: Target, animate: Boolean): StatisticsFragment {
            val fragment = StatisticsFragment()
            val bundle = Bundle()
            bundle.putParcelable(StatisticsFragment.ARG_TARGET, item)
            bundle.putLongArray(StatisticsFragment.ARG_ROUND_IDS, roundIds.toLongArray())
            bundle.putBoolean(StatisticsFragment.ARG_ANIMATE, animate)
            fragment.arguments = bundle
            return fragment
        }
    }
}
