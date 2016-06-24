/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityScoreboardBinding;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.ScoreboardConfiguration;
import de.dreier.mytargets.utils.ToolbarUtils;

import static android.support.v7.preference.PreferenceFragmentCompat.ARG_PREFERENCE_ROOT;

public class ScoreboardActivity extends AppCompatActivity {

    public static final String TRAINING_ID = "training_id";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private long mTraining;
    private boolean pageLoaded = true;
    private ActivityScoreboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(TRAINING_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID, -1);
        }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return HtmlUtils.getScoreboard(ScoreboardActivity.this, mTraining,
                        ScoreboardConfiguration.fromDisplaySettings(getApplicationContext()));
            }

            @Override
            protected void onPostExecute(String s) {
                binding.webView
                        .loadDataWithBaseURL("file:///android_asset/", s, "text/html", "UTF-8", "");
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_print).setVisible(pageLoaded &&
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.KITKAT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_print:
                print();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SimpleFragmentActivityBase.SettingsActivity.class);
                i.putExtra(ARG_PREFERENCE_ROOT, "scoreboard");
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return HtmlUtils.getScoreboard(ScoreboardActivity.this, mTraining,
                        ScoreboardConfiguration.fromPrintSettings(getApplicationContext()));
            }

            @SuppressWarnings("deprecation")
            @Override
            protected void onPostExecute(String s) {
                webViewPrint
                        .loadDataWithBaseURL("file:///android_asset/", s, "text/html", "UTF-8", "");
                PrintDocumentAdapter printAdapter = webViewPrint.createPrintDocumentAdapter();

                // Create a print job with name and adapter instance
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
                String jobName = getString(R.string.scoreboard) + " Document";
                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        }.execute();
    }
}
