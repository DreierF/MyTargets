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
import android.os.Bundle;
import android.os.Parcelable;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditArrowActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditBowActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditTrainingActivity;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.FABMenu;

import static de.dreier.mytargets.fragments.EditArrowFragment.ARROW_ID;
import static de.dreier.mytargets.fragments.EditBowFragment.BOW_ID;
import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND;
import static de.dreier.mytargets.fragments.FragmentBase.ITEM_ID;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends AppCompatActivity
        implements FragmentBase.OnItemSelectedListener, FragmentBase.ContentListener, ViewPager.OnPageChangeListener, FABMenu.Listener {

    private static boolean shownThisTime = false;
    private final boolean[] empty = new boolean[3];
    private final int[] stringRes = new int[3];
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.slidingTabs)
    TabLayout tabLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(android.R.id.content)
    View rootView;

    FABMenu fm;

    {
        stringRes[0] = R.string.new_training;
        stringRes[1] = R.string.new_bow;
        stringRes[2] = R.string.new_arrow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fm = new FABMenu(this, rootView);
        fm.setListener(this);
        fm.setFABItem(1, R.drawable.ic_trending_up_white_24dp, R.string.free_training);
        fm.setFABItem(2, R.drawable.ic_album_24dp, R.string.training_with_standard_round);

        MainTabsFragmentPagerAdapter adapter = new MainTabsFragmentPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState != null) {
            fm.onRestoreInstanceState(savedInstanceState);
        }

        askForHelpTranslating();
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

    @Override
    protected void onResume() {
        super.onResume();
        fm.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fm.unbind();
        ButterKnife.unbind(this);
    }

    private void askForHelpTranslating() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        //TODO update
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
            Linkify.addLinks(s, Linkify.WEB_URLS);
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
    public boolean isFABExpandable() {
        return viewPager.getCurrentItem() == 0;
    }

    @Override
    public void onFabClicked(int index) {
        switch (index) {
            case 0:
                int currentTab = viewPager.getCurrentItem();
                if (currentTab == 1) {
                    startActivityAnimated(EditBowActivity.class);
                } else if (currentTab == 2) {
                    startActivityAnimated(EditArrowActivity.class);
                }
                break;
            case 1:
                startActivityAnimated(EditTrainingActivity.class, TRAINING_TYPE, FREE_TRAINING);
                break;
            case 2:
                startActivityAnimated(EditTrainingActivity.class, TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND);
                break;
        }
    }

    private void startActivityAnimated(Class<?> aClass) {
        Intent i = new Intent(this, aClass);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void startActivityAnimated(Class<?> aClass, String key, long value) {
        Intent i = new Intent(this, aClass);
        i.putExtra(key, value);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void startActivityAnimated(Class<?> aClass, String key, int value) {
        Intent i = new Intent(this, aClass);
        i.putExtra(key, value);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onItemSelected(Parcelable passedItem) {
        IIdProvider item = Parcels.unwrap(passedItem);
        if (item instanceof Arrow) {
            startActivityAnimated(EditArrowActivity.class, ARROW_ID, item.getId());
        } else if (item instanceof Bow) {
            startActivityAnimated(EditBowActivity.class, BOW_ID, item.getId());
        } else {
            startActivityAnimated(EditTrainingActivity.class, ITEM_ID, item.getId());
        }
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        for (int i = 0; i < this.stringRes.length; i++) {
            if (stringRes == this.stringRes[i]) {
                this.empty[i] = empty;
            }
        }
        fm.notifyContentChanged();
        onPageSelected(viewPager.getCurrentItem());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fm.setFABHelperTitle(empty[position] ? stringRes[position] : 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.onSaveInstanceState(outState);
    }
}
