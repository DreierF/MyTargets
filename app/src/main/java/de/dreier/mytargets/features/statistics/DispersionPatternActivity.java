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

package de.dreier.mytargets.features.statistics;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.print.PrintHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding;
import de.dreier.mytargets.features.scoreboard.EFileType;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.utils.FileUtilsKt;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

public class DispersionPatternActivity extends ChildActivityBase {

    private static final String ITEM = "item";
    private ActivityArrowRankingDetailsBinding binding;
    private ArrowStatistic statistic;

    @NonNull
    public static IntentWrapper getIntent(ArrowStatistic statistics) {
        return new IntentWrapper(DispersionPatternActivity.class)
                .with(ITEM, statistics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_arrow_ranking_details);

        statistic = getIntent().getParcelableExtra(ITEM);

        ToolbarUtils.showHomeAsUp(this);
        if (statistic.getArrowName() != null) {
            ToolbarUtils.setTitle(this, getString(R.string.arrow_number_x, statistic
                    .getArrowNumber()));
            ToolbarUtils.setSubtitle(this, statistic.getArrowName());
        } else {
            ToolbarUtils.setTitle(this, R.string.dispersion_pattern);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        EAggregationStrategy strategy = SettingsManager.INSTANCE
                .getStatisticsDispersionPatternAggregationStrategy();
        TargetImpactAggregationDrawable drawable = statistic.getTarget().getImpactAggregationDrawable();
        drawable.setAggregationStrategy(strategy);
        drawable.replaceShotsWith(statistic.getShots());
        drawable.setArrowDiameter(statistic.getArrowDiameter(), SettingsManager.INSTANCE
                .getInputArrowDiameterScale());

        binding.dispersionView.setImageDrawable(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_print).setVisible(Utils.isKitKat());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareImage();
                return true;
            case R.id.action_print:
                print();
                return true;
            case R.id.action_settings:
                SettingsActivity.getIntent(ESettingsScreens.STATISTICS)
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called after the user selected with items he wants to share */
    private void shareImage() {
        new Thread(() -> {
            try {
                File dir = getCacheDir();
                EFileType fileType = SettingsManager.INSTANCE.getStatisticsDispersionPatternFileType();
                final File f = File.createTempFile("dispersion_pattern",
                        "." + fileType.name().toLowerCase(), dir);
                if (fileType == EFileType.PDF && Utils.isKitKat()) {
                    DispersionPatternUtils.generatePdf(f, statistic);
                } else {
                    DispersionPatternUtils.createDispersionPatternImageFile(800, f, statistic);
                }

                // Build and fire intent to ask for share provider
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(fileType.getMimeType());
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        FileUtilsKt.toUri(f, DispersionPatternActivity.this));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void print() {
        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        // Get the image
        Bitmap image = DispersionPatternUtils.getDispersionPatternBitmap(800, statistic);
        if (image != null) {
            // Send it to the print helper
            printHelper.printBitmap("Dispersion Pattern", image);
        }
    }
}
