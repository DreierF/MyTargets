/*
 * Copyright (C) 2017 Florian Dreier
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

import android.annotation.SuppressLint
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Intent
import android.content.Loader
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat.END
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.databinding.ActivityStatisticsBinding
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.utils.toSparseArray
import de.dreier.mytargets.shared.utils.toUri
import de.dreier.mytargets.utils.ToolbarUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : ChildActivityBase(), LoaderManager.LoaderCallbacks<List<Pair<Training, Round>>> {

    private lateinit var binding: ActivityStatisticsBinding
    private var rounds: List<Pair<Training, Round>>? = null
    private var filteredRounds: List<Pair<Target, List<Round>>>? = null

    @State
    var distanceTags: HashSet<String>? = null

    @State
    var diameterTags: HashSet<String>? = null

    @State
    var arrowTags: HashSet<Long?>? = null

    @State
    var bowTags: HashSet<Long?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)
        setSupportActionBar(binding.toolbar)

        binding.reset.setOnClickListener { resetFilter() }

        binding.progressBar.show()

        ToolbarUtils.showHomeAsUp(this)
        StateSaver.restoreInstanceState(this, savedInstanceState)

        loaderManager.initLoader(0, intent.extras, this).forceLoad()
    }

    @SuppressLint("StaticFieldLeak")
    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<List<Pair<Training, Round>>> {
        val roundIds = intent.getLongArrayExtra(ROUND_IDS)
        return object : AsyncTaskLoader<List<Pair<Training, Round>>>(this) {
            override fun loadInBackground(): List<Pair<Training, Round>> {
                val rounds = Round.getAll(roundIds)
                val trainingsMap = rounds.map { (_, trainingId) -> trainingId!! }
                        .distinct()
                        .map { id -> Pair(id, Training[id]!!) }
                        .toSparseArray()
                return rounds.map { round -> Pair(trainingsMap.get(round.trainingId!!), round) }
            }
        }
    }

    override fun onLoadFinished(loader: Loader<List<Pair<Training, Round>>>, data: List<Pair<Training, Round>>) {
        rounds = data
        binding.progressBar.hide()
        binding.distanceTags.tags = getDistanceTags()
        binding.distanceTags.setOnTagClickListener({ applyFilter() })
        binding.diameterTags.tags = getDiameterTags()
        binding.diameterTags.setOnTagClickListener({ applyFilter() })
        binding.arrowTags.tags = getArrowTags()
        binding.arrowTags.setOnTagClickListener({ applyFilter() })
        binding.bowTags.tags = getBowTags()
        binding.bowTags.setOnTagClickListener({ applyFilter() })

        if (distanceTags != null && diameterTags != null && arrowTags != null && bowTags != null) {
            restoreCheckedStates()
        }

        applyFilter()
        invalidateOptionsMenu()
    }

    private fun restoreCheckedStates() {
        binding.distanceTags.tags.forEach { it.isChecked = distanceTags!!.contains(it.text) }
        binding.diameterTags.tags.forEach { it.isChecked = diameterTags!!.contains(it.text) }
        binding.arrowTags.tags.forEach { it.isChecked = arrowTags!!.contains(it.id) }
        binding.bowTags.tags.forEach { it.isChecked = bowTags!!.contains(it.id) }
        binding.distanceTags.tags = binding.distanceTags.tags
        binding.diameterTags.tags = binding.diameterTags.tags
        binding.arrowTags.tags = binding.arrowTags.tags
        binding.bowTags.tags = binding.bowTags.tags
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.export_filter, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val filter = menu.findItem(R.id.action_filter)
        val export = menu.findItem(R.id.action_export)
        // only show filter if we have at least one category to filter by
        val filterAvailable = (binding.distanceTags.tags.size > 1
                || binding.diameterTags.tags.size > 1
                || binding.bowTags.tags.size > 1
                || binding.arrowTags.tags.size > 1)
        filter.isVisible = rounds != null && filterAvailable
        export.isVisible = rounds != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                export()
                true
            }
            R.id.action_filter -> {
                if (!binding.drawerLayout.isDrawerOpen(END)) {
                    binding.drawerLayout.openDrawer(END)
                } else {
                    binding.drawerLayout.closeDrawer(END)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetFilter() {
        binding.distanceTags.tags.forEach { it.isChecked = true }
        binding.diameterTags.tags.forEach { it.isChecked = true }
        binding.arrowTags.tags.forEach { it.isChecked = true }
        binding.bowTags.tags.forEach { it.isChecked = true }
        binding.distanceTags.tags = binding.distanceTags.tags
        binding.diameterTags.tags = binding.diameterTags.tags
        binding.arrowTags.tags = binding.arrowTags.tags
        binding.bowTags.tags = binding.bowTags.tags
        applyFilter()
    }

    private fun applyFilter() {
        distanceTags = binding.distanceTags.checkedTags.map { t -> t.text }.toHashSet()
        diameterTags = binding.diameterTags.checkedTags.map { t -> t.text }.toHashSet()
        arrowTags = binding.arrowTags.checkedTags.map { t -> t.id }.toHashSet()
        bowTags = binding.bowTags.checkedTags.map { t -> t.id }.toHashSet()
        filteredRounds = rounds!!
                .filter { (training, round) ->
                    distanceTags!!.contains(round.distance.toString())
                            && diameterTags!!.contains(round.target.diameter.toString())
                            && arrowTags!!.contains(training.arrowId)
                            && bowTags!!.contains(training.bowId)
                }
                .map { p -> p.second }
                .groupBy { value -> Pair(value.target.id, value.target.getScoringStyle()) }
                .map { value1 -> Pair(value1.value[0].target, value1.value) }
                .sortedByDescending { it.second.size }
        val animate = binding.viewPager.adapter == null
        val adapter = StatisticsPagerAdapter(
                supportFragmentManager, filteredRounds!!, animate)
        binding.viewPager.adapter = adapter
    }

    private fun getBowTags(): List<Tag> {
        return rounds!!
                .map { it.first.bowId }
                .distinct()
                .map { bid ->
                    if (bid != null) {
                        val bow = Bow[bid] ?: return@map Tag(bid, "Deleted " + bid)
                        Tag(bow.id, bow.name, bow.thumbnail!!.blob.blob, true)
                    } else {
                        Tag(null, getString(R.string.unknown))
                    }
                }
    }

    private fun getArrowTags(): List<Tag> {
        return rounds!!
                .map { it.first.arrowId }
                .distinct()
                .map { aid ->
                    if (aid != null) {
                        val arrow = Arrow[aid] ?: return@map Tag(aid, "Deleted " + aid)
                        Tag(arrow.id, arrow.name, arrow.thumbnail!!.blob.blob, true)
                    } else {
                        Tag(null, getString(R.string.unknown))
                    }
                }
    }

    private fun getDistanceTags(): List<Tag> {
        return rounds!!
                .map { it.second.distance }
                .distinct()
                .sorted()
                .map { d -> Tag(d.id, d.toString()) }
    }

    private fun getDiameterTags(): List<Tag> {
        return rounds!!
                .map { it.second.target.diameter }
                .distinct()
                .sorted()
                .map { Tag(it.id, it.toString()) }
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState!!)
    }

    override fun onLoaderReset(loader: Loader<List<Pair<Training, Round>>>) {

    }

    @SuppressLint("StaticFieldLeak")
    internal fun export() {
        val progress = MaterialDialog.Builder(this)
                .content(R.string.exporting)
                .progress(true, 0)
                .show()
        object : AsyncTask<Void, Void, Uri>() {

            override fun doInBackground(vararg params: Void): Uri? {
                return try {
                    val f = File(cacheDir, exportFileName)
                    CsvExporter(applicationContext)
                            .exportAll(f, filteredRounds!!.flatMap { it.second }.map { it.id })
                    f.toUri(this@StatisticsActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }

            override fun onPostExecute(uri: Uri?) {
                super.onPostExecute(uri)
                progress.dismiss()
                if (uri != null) {
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_STREAM, uri)
                    email.type = "text/csv"
                    startActivity(Intent.createChooser(email, getString(R.string.send_exported)))
                } else {
                    Snackbar.make(binding.root, R.string.exporting_failed,
                            Snackbar.LENGTH_LONG).show()
                }
            }
        }.execute()
    }

    private val exportFileName: String
        get() {
            val format = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "MyTargets_exported_data_" + format.format(Date()) + ".csv"
        }

    inner class StatisticsPagerAdapter internal constructor(fm: FragmentManager, private val targets: List<Pair<Target, List<Round>>>, private val animate: Boolean) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val item = targets[position]
            val roundIds = item.second.map { it.id }
            return StatisticsFragment.newInstance(roundIds, item.first, animate)
        }

        override fun getCount(): Int {
            return targets.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return targets[position].first.toString()
        }
    }

    companion object {
        const val ROUND_IDS = "round_ids"
    }
}
