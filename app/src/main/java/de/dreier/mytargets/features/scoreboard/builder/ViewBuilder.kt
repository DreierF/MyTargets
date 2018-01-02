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

package de.dreier.mytargets.features.scoreboard.builder

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import de.dreier.mytargets.R
import de.dreier.mytargets.features.scoreboard.ScoreboardBuilder
import de.dreier.mytargets.features.scoreboard.builder.model.EndCell
import de.dreier.mytargets.features.scoreboard.builder.model.Table
import de.dreier.mytargets.features.scoreboard.builder.model.TextCell
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.db.Signature
import de.dreier.mytargets.shared.utils.CircleDrawable

class ViewBuilder(private val context: Context) : ScoreboardBuilder {
    private val root = LinearLayout(context)
    private var container: LinearLayout
    private val density: Float

    init {
        this.root.orientation = LinearLayout.VERTICAL
        this.container = root
        this.density = context.resources.displayMetrics.density
    }

    override fun title(title: String) {
        val titleView = TextView(context)
        titleView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        titleView.setTextColor(-0x1000000)
        titleView.setPadding(0, dp(8), 0, dp(8))
        titleView.setTextSize(COMPLEX_UNIT_SP, 16f)
        titleView.setTypeface(null, Typeface.BOLD)
        titleView.text = title
        container.addView(titleView)
    }

    override fun openSection() {
        container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        root.addView(container)
    }

    override fun closeSection() {
        container = root
    }

    override fun subtitle(subtitle: String) {
        val subtitleView = TextView(context)
        subtitleView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        subtitleView.setTextColor(-0x1000000)
        subtitleView.setPadding(0, dp(8), 0, dp(8))
        subtitleView.setTextSize(COMPLEX_UNIT_SP, 14f)
        subtitleView.setTypeface(null, Typeface.BOLD)
        subtitleView.text = subtitle
        container.addView(subtitleView)
    }

    override fun table(table: Table) {
        val tableLayout = createTableLayout(table)
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        if (table.wrapContent) {
            params.setMargins(0, 0, 0, dp(-2))
        }
        tableLayout.layoutParams = params
        container.addView(tableLayout)
    }

    private fun createTableLayout(table: Table): TableLayout {
        val tableLayout = TableLayout(context)
        tableLayout.setBackgroundResource(R.drawable.table_cell_border)
        if (!table.wrapContent) {
            tableLayout.isShrinkAllColumns = true
            tableLayout.isStretchAllColumns = true
        }

        table.rows.map { createTableRow(it) }
                .forEach { tableLayout.addView(it) }
        return tableLayout
    }

    private fun createTableRow(row: Table.Row): TableRow {
        val tableRow = TableRow(context)
        tableRow.layoutParams = TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        for (cell in row.cells) {
            val cellView = when (cell) {
                is EndCell -> createEndCell(cell)
                is Table -> {
                    val cellView = createTableLayout(cell)
                    // Avoid duplicated borders
                    val padding = (-density).toInt()
                    cellView.setPadding(padding, padding, padding, padding)
                    cellView
                }
                is TextCell -> createTextCell(cell)
            }
            val params = TableRow.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            if (cell.columnSpan > 1) {
                params.weight = 1f
                params.span = cell.columnSpan
            }
            cellView.layoutParams = params
            tableRow.addView(cellView)
        }
        return tableRow
    }

    private fun createEndCell(endCell: EndCell): ImageView {
        val imageView = ImageView(context)
        imageView.setBackgroundResource(R.drawable.table_cell_border)
        imageView.minimumWidth = dp(20)
        imageView.minimumHeight = dp(20)
        imageView.setPadding(dp(4), dp(4), dp(4), dp(4))
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView
                .setImageDrawable(CircleDrawable(density, endCell.score, endCell
                        .arrowNumber, endCell.fillColor, endCell.textColor))
        return imageView
    }

    private fun createTextCell(textCell: TextCell): TextView {
        val textView = TextView(context)
        textView.setBackgroundResource(R.drawable.table_cell_border)
        textView.minimumWidth = dp(20)
        textView.minimumHeight = dp(20)
        textView.gravity = Gravity.CENTER
        textView.setTextColor(-0x1000000)
        textView.setPadding(dp(4), dp(4), dp(4), dp(4))
        textView.text = textCell.content

        if (textCell.bold) {
            textView.setTypeface(null, Typeface.BOLD)
        }
        return textView
    }

    override fun signature(archerSignature: Signature, witnessSignature: Signature) {
        var archer = SettingsManager.profileFullName
        if (archer.trim { it <= ' ' }.isEmpty()) {
            archer = context.getString(R.string.archer)
        }
        val targetCaptain = context.getString(R.string.target_captain)

        val v = LayoutInflater.from(context).inflate(R.layout.partial_scoreboard_signatures, container, true)
        val archerDescriptionView = v.findViewById<TextView>(R.id.archer_description)
        archerDescriptionView.text = archerSignature.getName(archer)
        val witnessDescriptionView = v.findViewById<TextView>(R.id.witness_description)
        witnessDescriptionView.text = witnessSignature.getName(targetCaptain)
        val archerSignatureView = v.findViewById<ImageView>(R.id.signature_archer)
        archerSignatureView.setImageBitmap(archerSignature.bitmap)
        val witnessSignatureView = v.findViewById<ImageView>(R.id.signature_witness)
        witnessSignatureView.setImageBitmap(witnessSignature.bitmap)
    }

    private fun dp(dips: Int): Int {
        return (dips * density).toInt()
    }

    fun build(): LinearLayout {
        return container
    }
}
