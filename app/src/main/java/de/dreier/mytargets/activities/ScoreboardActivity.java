package de.dreier.mytargets.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.ScoreboardHelper;


public class ScoreboardActivity extends ActionBarActivity {

    public static final String ROUND_ID = "round_id";

    private long mRound;
    private WebView webView;
    private boolean pageLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ROUND_ID)) {
            mRound = intent.getLongExtra(ROUND_ID, -1);
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;
                supportInvalidateOptionsMenu();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadScoreboardTask.execute();
    }

    private final AsyncTask<Void, Void, String> loadScoreboardTask = new AsyncTask<Void, Void, String>() {

        @Override
        protected String doInBackground(Void... params) {
            return ScoreboardHelper.getHTMLString(ScoreboardActivity.this, mRound, true);
        }

        @Override
        protected void onPostExecute(String s) {
            webView.loadDataWithBaseURL("file:///android_asset/", s, "text/html", "UTF-8", "");
        }
    };

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
        if (item.getItemId() == R.id.action_print) {
            print();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void print() {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.scoreboard) + " Document";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
}
