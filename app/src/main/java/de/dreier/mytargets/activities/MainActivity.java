package de.dreier.mytargets.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.TrainingAdapter;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends NowListActivity {

    private static boolean shownThisTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean shown = prefs.getBoolean("translation_dialog_shown", false);

        String longLang = Locale.getDefault().getDisplayLanguage().toLowerCase();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!shortLocale.equals("de") && !shortLocale.equals("en") && !shown && !shownThisTime) {
            // Link the e-mail address in the message
            final SpannableString s = new SpannableString(Html.fromHtml("If you would like " +
                    "to help make MyTargets even better by translating the app to " +
                    longLang + ", please send me an E-Mail (dreier.florian@gmail.com) " +
                    "so I can give you access to the translation tool!<br /><br />" +
                    "Thanks in advance :)"));
            Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
            AlertDialog d = new AlertDialog.Builder(this).setTitle("App translation")
                    .setMessage(s)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.edit().putBoolean("translation_dialog_shown", true).apply();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shownThisTime = true;
                            dialog.dismiss();
                        }
                    }).create();
            d.show();
            ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.training_singular);
        itemPlural = getString(R.string.training_plural);
        mEnableBackAnimation = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new TrainingAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db = new DatabaseManager(MainActivity.this);

                        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
                        String fileName = "/MyTargets/exported_data_" + format.format(new Date()) + ".csv";
                        File file = new File(baseDir + fileName);
                        try {
                            db.exportAll(file);
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            email.setType("text/csv");
                            startActivity(Intent.createChooser(email, getString(R.string.send_exported)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.exporting_failed, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteTrainings(ids);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if (pos == 0) {
            i.setClass(this, NewRoundActivity.class);
        } else if (pos == 1) {
            i.setClass(this, BowActivity.class);
        } else {
            i.setClass(this, TrainingActivity.class);
            i.putExtra(TrainingActivity.TRAINING_ID, getListAdapter().getItemId(pos));
        }
        return true;
    }
}
