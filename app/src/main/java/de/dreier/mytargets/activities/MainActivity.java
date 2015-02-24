package de.dreier.mytargets.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.TrainingsFragment;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.views.SlidingTabLayout;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends ActionBarActivity {

    private static boolean shownThisTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainTabsFragmentPagerAdapter(this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        askForHelpTranslating();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public static class MainTabsFragmentPagerAdapter extends FragmentPagerAdapter {
        private final Context context;

        MainTabsFragmentPagerAdapter(ActionBarActivity context) {
            super(context.getSupportFragmentManager());
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new TrainingsFragment();
            } else if (position == 1) {
                return new BowFragment();
            } else {
                return new ArrowFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return context.getString(R.string.training);
            } else if (position == 1) {
                return context.getString(R.string.bow);
            } else {
                return context.getString(R.string.arrow);
            }
        }
    }

    private void askForHelpTranslating() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        supportedLanguages.add("de");
        supportedLanguages.add("en");
        supportedLanguages.add("fr");

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean shown = prefs.getBoolean("translation_dialog_shown", false);

        String longLang = Locale.getDefault().getDisplayLanguage();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!supportedLanguages.contains(shortLocale) && !shown && !shownThisTime) {
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
                        DatabaseManager db = new DatabaseManager(MainActivity.this);
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
}
