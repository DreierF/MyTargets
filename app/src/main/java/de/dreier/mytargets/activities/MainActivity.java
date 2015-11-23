/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.fragments.EditArrowFragment;
import de.dreier.mytargets.fragments.EditBowFragment;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.fragments.TrainingsFragment;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.IIdProvider;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends AppCompatActivity
        implements FragmentBase.OnItemSelectedListener, FragmentBase.ContentListener,
        View.OnClickListener, ViewPager.OnPageChangeListener {

    private static boolean shownThisTime = false;
    private View mNewLayout;
    private TextView mNewText;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        MainTabsFragmentPagerAdapter adapter = new MainTabsFragmentPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabTextColors(0xCCFFFFFF, Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);

        askForHelpTranslating();
        findViewById(R.id.fab).setOnClickListener(this);
        mNewLayout = findViewById(R.id.new_layout);
        mNewText = (TextView) findViewById(R.id.new_text);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(new Intent(this, SimpleFragmentActivity.SettingsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void askForHelpTranslating() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        Collections.addAll(supportedLanguages, "de", "en", "fr", "es", "ru", "nl", "it", "sl", "ca",
                "zh", "tr", "hu", "sl");
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        boolean shown = prefs.getBoolean("translation_dialog_shown", false);
        String longLang = Locale.getDefault().getDisplayLanguage();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!supportedLanguages.contains(shortLocale) && !shown && !shownThisTime) {
            // Link the e-mail address in the message
            final SpannableString s = new SpannableString(Html.fromHtml("If you would like " +
                    "to help make MyTargets even better by translating the app to " +
                    longLang +
                    " visit <a href=\"https://crowdin.com/project/mytargets\">crowdin</a>!<br /><br />" +
                    "Thanks in advance :)"));
            Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
            AlertDialog d = new AlertDialog.Builder(this)
                    .setTitle("App translation")
                    .setMessage(s)
                    .setPositiveButton("OK", (dialog, which) -> {
                        prefs.edit().putBoolean("translation_dialog_shown", true).apply();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Remind me later", (dialog, which) -> {
                        shownThisTime = true;
                        dialog.dismiss();
                    }).create();
            d.show();
            ((TextView) d.findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        if (viewPager.getCurrentItem() == 0) {
            i.setClass(this, SimpleFragmentActivity.EditTrainingActivity.class);
        } else if (viewPager.getCurrentItem() == 1) {
            i.setClass(this, SimpleFragmentActivity.EditBowActivity.class);
        } else if (viewPager.getCurrentItem() == 2) {
            i.setClass(this, SimpleFragmentActivity.EditArrowActivity.class);
        }
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onItemSelected(IIdProvider item) {
        Intent i;
        if (item instanceof Arrow) {
            i = new Intent(this, SimpleFragmentActivity.EditArrowActivity.class);
            i.putExtra(EditArrowFragment.ARROW_ID, item.getId());
        } else if (item instanceof Bow) {
            i = new Intent(this, SimpleFragmentActivity.EditBowActivity.class);
            i.putExtra(EditBowFragment.BOW_ID, item.getId());
        } else {
            i = new Intent(this, SimpleFragmentActivity.EditTrainingActivity.class);
            i.putExtra(TrainingsFragment.ITEM_ID, item.getId());
        }
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private final boolean[] empty = new boolean[3];
    private final int[] stringRes = new int[3];

    {
        stringRes[0] = R.string.new_training;
        stringRes[1] = R.string.new_bow;
        stringRes[2] = R.string.new_arrow;
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        for (int i = 0; i < this.stringRes.length; i++) {
            if (stringRes == this.stringRes[i]) {
                this.empty[i] = empty;
            }
        }
        onPageSelected(viewPager.getCurrentItem());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNewLayout.setVisibility(empty[position] ? View.VISIBLE : View.GONE);
        mNewText.setText(stringRes[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
