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

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding
import de.dreier.mytargets.features.scoreboard.EFileType
import de.dreier.mytargets.features.settings.ESettingsScreens
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.utils.print.CustomPrintDocumentAdapter
import de.dreier.mytargets.utils.print.DrawableToPdfWriter
import de.dreier.mytargets.utils.toUri
import java.io.File
import java.io.IOException

class DispersionPatternActivity : ChildActivityBase() {
    private var binding: ActivityArrowRankingDetailsBinding? = null
    private lateinit var statistic: ArrowStatistic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_arrow_ranking_details)

        statistic = intent.getParcelableExtra(ITEM)

        ToolbarUtils.showHomeAsUp(this)
        if (statistic.arrowName != null) {
            ToolbarUtils.setTitle(
                this, getString(
                    R.string.arrow_number_x, statistic
                        .arrowNumber
                )
            )
            ToolbarUtils.setSubtitle(this, statistic.arrowName!!)
        } else {
            ToolbarUtils.setTitle(this, R.string.dispersion_pattern)
        }
    }

    override fun onResume() {
        super.onResume()
        val drawable = DispersionPatternUtils.targetFromArrowStatistics(statistic)
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
            R.id.action_print -> if (Utils.isKitKat) print()
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
                    DispersionPatternUtils.generatePdf(f, statistic)
                } else {
                    DispersionPatternUtils.createDispersionPatternImageFile(1200, f, statistic)
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun print() {
        val target = DispersionPatternUtils.targetFromArrowStatistics(statistic)
        val fileName = getDefaultFileName(EFileType.PDF)
        val pda = CustomPrintDocumentAdapter(DrawableToPdfWriter(target), fileName)

        // Create a print job with name and adapter instance
        val printManager = getSystemService<PrintManager>()!!
        val jobName = "Dispersion Pattern"
        printManager.print(jobName, pda, PrintAttributes.Builder().build())
    }

    private fun getDefaultFileName(extension: EFileType): String {
        var name = if (statistic.arrowName != null) {
            statistic.arrowName!! + "-" + getString(R.string.arrow_number_x, statistic.arrowNumber)
        } else {
            statistic.exportFileName
        }
        if (!name.isNullOrEmpty()) {
            name = "-" + name
        }
        return getString(R.string.dispersion_pattern) + name + "." + extension.name.toLowerCase()
    }

    companion object {
        const val ITEM = "item"
    }
}
