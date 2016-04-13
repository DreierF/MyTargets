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
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditArrowActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditBowActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity.EditTrainingActivity;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.IIdProvider;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
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
        implements FragmentBase.OnItemSelectedListener, FragmentBase.ContentListener, ViewPager.OnPageChangeListener {

    private static boolean shownThisTime = false;
    private final boolean[] empty = new boolean[3];
    private final int[] stringRes = new int[3];
    @Bind(R.id.new_layout)
    View mNewLayout;
    @Bind(R.id.new_text)
    TextView mNewText;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.slidingTabs)
    TabLayout tabLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.fab1)
    FloatingActionButton fab1;
    @Bind(R.id.fab2)
    FloatingActionButton fab2;
    @Bind(R.id.fab1Label)
    TextView fab1Label;
    @Bind(R.id.fab2Label)
    TextView fab2Label;
    @Bind(R.id.overlayView)
    View overlayView;

    private boolean isFabOpen = false;
    private Animation fabOpen;
    private Animation fabClose;
    private Animation rotateForward;
    private Animation rotateBackward;
    private Animation fabShowAnimation;
    private Animation fabHideAnimation;

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

        if (savedInstanceState != null) {
            isFabOpen = savedInstanceState.getBoolean("fab_open");
        }

        MainTabsFragmentPagerAdapter adapter = new MainTabsFragmentPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);

        askForHelpTranslating();
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fabShowAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_label_show);
        fabHideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_label_hide);

        setSupportActionBar(toolbar);
        applyFabState(isFabOpen);
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
        applyFabState(isFabOpen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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

    @OnClick({R.id.fab, R.id.fab1, R.id.fab2, R.id.fab1Label, R.id.fab2Label})
    public void onClick(View v) {
        int currentTab = viewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.fab:
                if (currentTab == 0) {
                    animateFAB();
                } else if (currentTab == 1) {
                    startActivityAnimated(EditBowActivity.class);
                } else if (currentTab == 2) {
                    startActivityAnimated(EditArrowActivity.class);
                }
                break;
            case R.id.fab1Label:
            case R.id.fab1:
                startActivityAnimated(EditTrainingActivity.class, TRAINING_TYPE, FREE_TRAINING);
                applyFabState(false);
                break;
            case R.id.fab2Label:
            case R.id.fab2:
                startActivityAnimated(EditTrainingActivity.class, TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND);
                applyFabState(false);
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

    public void animateFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1Label.startAnimation(fabHideAnimation);
            fab2Label.startAnimation(fabHideAnimation);
            applyFabState(false);
        } else {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1Label.startAnimation(fabShowAnimation);
            fab2Label.startAnimation(fabShowAnimation);
            applyFabState(true);
        }
    }

    public void applyFabState(boolean state) {
        isFabOpen = state;
        final int visibility = isFabOpen ? VISIBLE : INVISIBLE;
        fab1Label.setVisibility(visibility);
        fab2Label.setVisibility(visibility);
        fab.setRotation(isFabOpen ? 135f : 0f);
        fab1.setClickable(isFabOpen);
        fab2.setClickable(isFabOpen);
        fab1Label.setClickable(isFabOpen);
        fab2Label.setClickable(isFabOpen);
        overlayView.setVisibility(visibility);
    }

    @OnClick(R.id.overlayView)
    public void dismissFabMenu() {
        animateFAB();
    }

    @Override
    public void onItemSelected(IIdProvider item) {
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
        if (isFabOpen) {
            animateFAB();
        }
        onPageSelected(viewPager.getCurrentItem());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNewLayout.setVisibility(empty[position] ? VISIBLE : View.GONE);
        mNewText.setText(stringRes[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fab_open", isFabOpen);
    }
}
