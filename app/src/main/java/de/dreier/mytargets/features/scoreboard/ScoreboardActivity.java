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

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.databinding.ActivityScoreboardBinding;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;

import static android.support.v4.content.FileProvider.getUriForFile;

public class ScoreboardActivity extends ChildActivityBase {

    @VisibleForTesting
    public static final String TRAINING_ID = "training_id";
    @VisibleForTesting
    public static final String ROUND_ID = "round_id";
    private static final int SCOREBOARD_LOADER_ID = 1;
    private static final int SCOREBOARD_IMAGE_LOADER_ID = 2;
    private static final int SCOREBOARD_PRINT_LOADER_ID = 3;

    private long mTraining;
    private long mRound;
    private boolean pageLoaded = true;
    private ActivityScoreboardBinding binding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard);

        Intent intent = getIntent();
        mTraining = intent.getLongExtra(TRAINING_ID, -1);
        mRound = intent.getLongExtra(ROUND_ID, -1);

        binding.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;
                supportInvalidateOptionsMenu();
            }
        });

        ToolbarUtils.showHomeAsUp(this);

        getLoaderManager().initLoader(SCOREBOARD_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<String>() {
                    @Override
                    public Loader<String> onCreateLoader(int id, Bundle args) {
                        return new AsyncTaskLoader<String>(ScoreboardActivity.this) {
                            @Override
                            public String loadInBackground() {
                                return HtmlUtils.getScoreboard(mTraining, mRound,
                                        ScoreboardConfiguration.fromDisplaySettings());
                            }
                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<String> loader, String data) {
                        binding.webView
                                .loadDataWithBaseURL("file:///android_asset/", data, "text/html",
                                        "UTF-8", "");
                    }

                    @Override
                    public void onLoaderReset(Loader<String> loader) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_print).setVisible(pageLoaded &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareImage();
                return true;
            case R.id.action_print:
                print();
                return true;
            case R.id.action_settings:
                SettingsActivity.getIntent(ESettingsScreens.SCOREBOARD)
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called after the user selected with items he wants to share */
    private void shareImage() {
        // Construct share intent

        getLoaderManager().initLoader(SCOREBOARD_IMAGE_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<Uri>() {

                    @Override
                    public Loader<Uri> onCreateLoader(int id, Bundle args) {
                        return new AsyncTaskLoader<Uri>(ScoreboardActivity.this) {
                            @Override
                            public Uri loadInBackground() {
                                try {
                                    File dir = getCacheDir();
                                    final File f = File.createTempFile("scoreboard", ".png", dir);
                                    new ScoreboardImage()
                                            .generateBitmap(ScoreboardActivity.this, mTraining,
                                                    mRound, f);
                                    String packageName = getApplicationContext().getPackageName();
                                    String authority = packageName + ".easyphotopicker.fileprovider";
                                    return getUriForFile(ScoreboardActivity.this, authority, f);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<Uri> loader, Uri data) {
                        if (data == null) {
                            Snackbar.make(binding.getRoot(), R.string.sharing_failed,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            // Build and fire intent to ask for share provider
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("*/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, data);
                            startActivity(
                                    Intent.createChooser(shareIntent, getString(R.string.share)));
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<Uri> loader) {

                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void print() {
        // Get a print adapter instance
        final WebView webViewPrint = new WebView(this);
        webViewPrint.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        webViewPrint.setLayoutParams(p);
        ((ViewGroup) binding.getRoot()).addView(webViewPrint);

        getLoaderManager().initLoader(SCOREBOARD_PRINT_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<String>() {
                    @Override
                    public Loader<String> onCreateLoader(int id, Bundle args) {
                        return new AsyncTaskLoader<String>(ScoreboardActivity.this) {
                            @Override
                            public String loadInBackground() {
                                return HtmlUtils.getScoreboard(mTraining, mRound,
                                        ScoreboardConfiguration.fromPrintSettings());
                            }
                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<String> loader, String data) {
                        webViewPrint
                                .loadDataWithBaseURL("file:///android_asset/", data, "text/html",
                                        "UTF-8", "");
                        PrintDocumentAdapter printAdapter = webViewPrint
                                .createPrintDocumentAdapter();

                        // Create a print job with name and adapter instance
                        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
                        String jobName = getString(R.string.scoreboard) + " Document";
                        printManager.print(jobName, printAdapter,
                                new PrintAttributes.Builder().build());
                    }

                    @Override
                    public void onLoaderReset(Loader<String> loader) {

                    }
                });
    }
}
