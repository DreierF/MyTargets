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

import android.annotation.TargetApi
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.print.PrintHelper
import android.view.Menu
import android.view.MenuItem
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding
import de.dreier.mytargets.features.scoreboard.EFileType
import de.dreier.mytargets.features.settings.ESettingsScreens
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.utils.toUri
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.Utils
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException

class DispersionPatternActivity : ChildActivityBase() {
    private var binding: ActivityArrowRankingDetailsBinding? = null
    private var statistic: ArrowStatistic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_arrow_ranking_details)

        statistic = intent.getParcelableExtra(ITEM)

        ToolbarUtils.showHomeAsUp(this)
        if (statistic!!.arrowName != null) {
            ToolbarUtils.setTitle(this, getString(R.string.arrow_number_x, statistic!!
                    .arrowNumber))
            ToolbarUtils.setSubtitle(this, statistic!!.arrowName!!)
        } else {
            ToolbarUtils.setTitle(this, R.string.dispersion_pattern)
        }
    }

    override fun onResume() {
        super.onResume()

        val strategy = SettingsManager.statisticsDispersionPatternAggregationStrategy
        val drawable = statistic!!.target.impactAggregationDrawable
        drawable.setAggregationStrategy(strategy)
        drawable.replaceShotsWith(statistic!!.shots)
        drawable.setArrowDiameter(statistic!!.arrowDiameter, SettingsManager.inputArrowDiameterScale)

        binding!!.dispersionView.setImageDrawable(drawable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scoreboard, menu)
        menu.findItem(R.id.action_print).isVisible = Utils.isKitKat
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> shareImage()
            R.id.action_print -> print()
            R.id.action_settings -> navigationController.navigateToSettings(ESettingsScreens.STATISTICS)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    /* Called after the user selected with items he wants to share */
    private fun shareImage() {
        Thread {
            try {
                val fileType = SettingsManager.statisticsDispersionPatternFileType
                val f = File(cacheDir, getDefaultFileName(fileType))
                if (fileType === EFileType.PDF && Utils.isKitKat) {
                    DispersionPatternUtils.generatePdf(f, statistic!!)
                } else {
                    DispersionPatternUtils.createDispersionPatternImageFile(800, f, statistic!!)
                }

                // Build and fire intent to ask for share provider
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = fileType.mimeType
                shareIntent.putExtra(Intent.EXTRA_STREAM, f.toUri(this@DispersionPatternActivity))
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            } catch (e: IOException) {
                e.printStackTrace()
                Snackbar.make(binding!!.root, R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                        .show()
            }
        }.start()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun print() {
        val printHelper = PrintHelper(this)
        printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT

        // Get the image
        val image = DispersionPatternUtils.getDispersionPatternBitmap(800, statistic!!)
        printHelper.printBitmap("Dispersion Pattern", image)
    }

    private fun getDefaultFileName(extension: EFileType): String {
        val name = if (statistic!!.arrowName != null) {
            statistic!!.arrowName!! + "-" + getString(R.string.arrow_number_x, statistic!!.arrowNumber)
        } else {
            getString(R.string.dispersion_pattern)
        }
        val formattedDate = statistic!!.date?.let {
            DateTimeFormatter.ISO_LOCAL_DATE.format(it)
        } ?: ""

        return formattedDate + name + "." + extension.name.toLowerCase()
    }

    companion object {
        const val ITEM = "item"
    }
}
