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

package de.dreier.mytargets.features.scoreboard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.databinding.ActivityScoreboardBinding;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.shared.utils.FileUtils.getUriForFile;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;

public class ScoreboardActivity extends ChildActivityBase {

    @VisibleForTesting
    public static final String TRAINING_ID = "training_id";
    @VisibleForTesting
    public static final String ROUND_ID = "round_id";

    private long trainingId;
    private long roundId;
    private boolean pageLoaded = true;
    private ActivityScoreboardBinding binding;
    private Training training;

    @NonNull
    public static IntentWrapper getIntent(long trainingId) {
        return getIntent(trainingId, -1);
    }

    @NonNull
    public static IntentWrapper getIntent(long trainingId, long roundId) {
        return new IntentWrapper(ScoreboardActivity.class)
                .with(TRAINING_ID, trainingId)
                .with(ROUND_ID, roundId);
    }

    @NonNull
    private BroadcastReceiver updateReceiver = new MobileWearableClient.EndUpdateReceiver() {

        @Override
        protected void onUpdate(Long training, Long round, End end) {
            if (roundId == round || (training == trainingId && roundId == -1)) {
                reloadData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard);

        Intent intent = getIntent();
        trainingId = intent.getLongExtra(TRAINING_ID, -1);
        roundId = intent.getLongExtra(ROUND_ID, -1);

//        binding.webView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                pageLoaded = true;
//                supportInvalidateOptionsMenu();
//            }
//        });

        ToolbarUtils.showHomeAsUp(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
    }

    @SuppressLint("StaticFieldLeak")
    private void reloadData() {
        binding.container.removeAllViews();
        training = Training.get(trainingId);
        HtmlUtils.getScoreboardView(this, Utils.getCurrentLocale(this),
                training, roundId, ScoreboardConfiguration.fromPrintSettings(), binding.container);

//        new AsyncTask<Void, Void, String>() {
//
//            @NonNull
//            @Override
//            protected String doInBackground(Void... params) {
//                return HtmlUtils.getHtmlScoreboard(ScoreboardActivity.this, Utils
//                                .getCurrentLocale(ScoreboardActivity.this), trainingId, roundId,
//                        ScoreboardConfiguration.fromDisplaySettings());
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                binding.webView
//                        .loadDataWithBaseURL("file:///android_asset/", s, "text/html", "UTF-8", "");
//            }
//        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_print).setVisible(pageLoaded && Utils.isKitKat());
//        menu.findItem(R.id.action_pdf).setVisible(pageLoaded && Utils.isKitKat());
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
//            case R.id.action_pdf:
//                exportPdf();
//                return true;
            case R.id.action_settings:
                SettingsActivity.getIntent(ESettingsScreens.SCOREBOARD)
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportPdf() {

    }

    /* Called after the user selected with items he wants to share */
    @SuppressLint("StaticFieldLeak")
    private void shareImage() {
        // Construct share intent
        new AsyncTask<Void, Void, Uri>() {

            @Nullable
            @Override
            protected Uri doInBackground(Void... objects) {
                try {
                    File dir = getCacheDir();
                    final File f = File.createTempFile("scoreboard", ".png", dir);
                    new ScoreboardImage()
                            .generateBitmap(ScoreboardActivity.this, trainingId, roundId, f);
                    return getUriForFile(ScoreboardActivity.this, f);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable Uri uri) {
                super.onPostExecute(uri);
                if (uri == null) {
                    Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    // Build and fire intent to ask for share provider
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void print() {
        String fileName = String
                .format(Locale.US, "%04d-%02d-%02d-%s", training.date.getYear(), training.date
                        .getMonthValue(), training.date
                        .getDayOfMonth(), getString(R.string.scoreboard));
        String jobName = getString(R.string.scoreboard) + " Document";
        PrintDocumentAdapter pda = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                OutputStream output = null;

                try {
                    output = new FileOutputStream(destination.getFileDescriptor());
                    getDocument(output);
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileName)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };

        // Create a print job with name and adapter instance
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print(jobName, pda, new PrintAttributes.Builder().build());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDocument(OutputStream outputStream) throws IOException {
        PdfDocument document = new PdfDocument();
        PageInfo pageInfo = new PageInfo.Builder(1500, 3000, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        View content = binding.container;
        content.draw(page.getCanvas());

        document.finishPage(page);
        document.writeTo(outputStream);
        document.close();
    }
}
