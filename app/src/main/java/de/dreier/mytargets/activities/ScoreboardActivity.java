package de.dreier.mytargets.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;

import de.dreier.mytargets.R;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.utils.TargetOpenHelper;


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
            TargetOpenHelper db = new TargetOpenHelper(ScoreboardActivity.this);
            Round info = db.getRound(mRound);
            Cursor cur = db.getPasses(mRound);
            String html = "<html><style type=\"text/css\">\n" +
                    ".myTable { border-collapse:collapse; width:100%; }\n" +
                    ".myTable td, .myTable th { padding:5px; border:1px solid #000; text-align: center; font-family: Roboto, Sans-serif; }\n" +
                    "</style>" +
                    "<table class=\"myTable\"><tr>";
            for (int i = 1; i <= info.ppp; i++) {
                html += "<th>" + i + "</th>";
            }
            html += getString(R.string.html_header_fields);
            html += "</tr>";
            int sum = 0, carry = 0, count = 0;
            int i = 0;
            String tmp_html = "";
            if (cur.moveToFirst()) {
                do {
                    int passe_id = cur.getInt(0);
                    int[] passe = db.getPasse(passe_id);
                    int arrows = 0;
                    if (i % 2 == 1) {
                        tmp_html += "<tr>";
                        for (int aPasse : passe) {
                            tmp_html += "<td>";
                            tmp_html += Target.getStringByZone(info.target, aPasse, info.compound);
                            tmp_html += "</td>";
                            int points = Target.getPointsByZone(info.target, aPasse, info.compound);
                            arrows += points;
                            sum += points;
                            carry += points;
                            count++;
                        }
                        tmp_html += "<td>" + arrows + "</td>";
                        html += "<td rowspan=\"2\">" + sum + "</td>";
                        html += "<td rowspan=\"2\">" + carry + "</td>";
                        html += tmp_html;
                        tmp_html = "";
                        sum = 0;
                    } else {
                        html += "<tr>";
                        for (int aPasse : passe) {
                            html += "<td>";
                            html += Target.getStringByZone(info.target, aPasse, info.compound);
                            html += "</td>";
                            int points = Target.getPointsByZone(info.target, aPasse, info.compound);
                            arrows += points;
                            sum += points;
                            carry += points;
                            count++;
                        }
                        html += "<td>" + arrows + "</td>";
                    }
                    i++;
                    tmp_html += "</tr>";
                } while (cur.moveToNext());
            }
            if (i % 2 == 1) {
                html += "<td rowspan=\"2\">" + sum + "</td>";
                html += "<td rowspan=\"2\">" + carry + "</td>";
            }
            html += tmp_html;
            db.close();
            float avg = ((carry * 100) / count) / 100.0f;
            html += "</table>";
            html += "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                    "<tr><th>" + getString(R.string.nine) + "</th>" +
                    "<th>" + getString(R.string.ten_x) + "</th>" +
                    "<th>X</th>" +
                    "<th>" + getString(R.string.average) + "</th></tr>" +
                    "<tr><td>" + info.scoreCount[2] + "</td>" +
                    "<td>" + (info.scoreCount[0] + info.scoreCount[1]) + "</td>" +
                    "<td>" + info.scoreCount[0] + "</td>" +
                    "<td>" + avg + "</td>" +
                    "</tr></table>";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new TargetImage().generateBitmap(ScoreboardActivity.this, 800, info, mRound, byteArrayOutputStream);

            // Convert bitmap to Base64 encoded image for web
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imgageBase64;
            html += "<div align='center' style=\"padding: 20px;\"><img src='" + image + "' width='60%' /></div>";
            html += "</html>";
            return html;
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
