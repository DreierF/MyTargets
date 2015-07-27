/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.views.TargetView;

public class InputActivity extends AppCompatActivity implements OnTargetSetListener {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String SHOW_ALL_MODE = "show_all";
    private static final String TIMER_ENABLED = "timer_enabled";
    private TargetView target;
    private Button next, prev;
    private int curPasse = 1;
    private int savedPasses = 0;
    private Round mRound;
    private DatabaseManager db;
    private boolean mShowAllMode = false;
    private WearMessageManager manager;
    private boolean mTimerEnabled;
    private Training training;
    private RoundTemplate template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        next = (Button) findViewById(R.id.next_button);
        prev = (Button) findViewById(R.id.prev_button);
        target = (TargetView) findViewById(R.id.target_view);
        target.setOnTargetSetListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = DatabaseManager.getInstance(this);

        Intent intent = getIntent();
        assert intent != null;
        long mTraining;
        if (intent.hasExtra(TRAINING_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID, -1);
            ArrayList<Round> rounds = db.getRounds(mTraining);
            training = db.getTraining(mTraining);
            for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
                mRound = rounds.get(roundIndex);
                template = mRound.info;
                ArrayList<Passe> passes = db.getPassesOfRound(mRound.getId());
                savedPasses = passes.size();
                if (savedPasses < template.passes || roundIndex + 1 == rounds.size()) {
                    curPasse = Math.min(savedPasses + 1, template.passes);
                    break;
                }
            }
        } else {
            long roundId = intent.getLongExtra(ROUND_ID, -1);
            curPasse = intent.getIntExtra(PASSE_IND, -1) + 1;
            mRound = db.getRound(roundId);
            template = mRound.info;
            mTraining = mRound.training;
            training = db.getTraining(mTraining);
            savedPasses = db.getPassesOfRound(roundId).size();
        }

        target.setRoundTemplate(template);
        mShowAllMode = prefs.getBoolean(SHOW_ALL_MODE, false);
        mTimerEnabled = prefs.getBoolean(TIMER_ENABLED, false);
        target.showAll(mShowAllMode);

        // Send message to wearable app, that we are starting a passe
        new Thread(() -> {
            startWearNotification();
        }).start();

        if (savedInstanceState != null) {
            target.restoreState(savedInstanceState);
            curPasse = savedInstanceState.getInt("curPasse");
            updatePasse();
        } else {
            setPasse(curPasse);
        }

        next.setOnClickListener(view -> setPasse(curPasse + 1));
        prev.setOnClickListener(view -> setPasse(curPasse - 1));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void startWearNotification() {
        Bitmap image;
        if (training.bow > 0) {
            Bow bow = DatabaseManager.getInstance(this).getBow(training.bow, true);
            image = bow.image;
        } else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        }
        image = ThumbnailUtils.extractThumbnail(image, 320, 320);

        NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, image, info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passe, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_switch_mode)
        //        .setIcon(mMode ? R.drawable.ic_target_zone_24dp : R.drawable.ic_target_exact_24dp);
        menu.findItem(R.id.action_show_all).setIcon(
                mShowAllMode ? R.drawable.ic_visibility_white_24dp :
                        R.drawable.ic_visibility_off_white_24dp);
        menu.findItem(R.id.action_timer).setIcon(
                mTimerEnabled ? R.drawable.ic_timer_white_24dp :
                        R.drawable.ic_timer_off_white_24dp);
        return true;
    }

    void setPasse(int passe) {
        if (passe <= savedPasses) {
            Passe p = db.getPasse(mRound.getId(), passe);
            if (p != null) {
                target.setPasse(p);
            } else {
                target.reset();
            }
        } else {
            if (passe != curPasse) {
                target.reset();
            }
            if (mTimerEnabled) {
                openTimer();
            }
        }
        ArrayList<Passe> oldOnes = db.getPasses(mRound.getId());
        target.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
    }

    void updatePasse() {
        setTitle(getString(R.string.passe_n, curPasse));
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse <= savedPasses && curPasse <= template.passes);

        if (savedPasses == template.passes) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    /*private void showStopDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources()
                        .getQuantityString(R.plurals.passes_finished, savedPasses, savedPasses))
                .setMessage(R.string.passes_finished_now_what)
                .setPositiveButton(R.string.scoreboard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(InputActivity.this, ScoreboardActivity.class);
                        intent.putExtra(ScoreboardActivity.TRAINING_ID, r.getParentId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.continue_with_next,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mStopAfter = -1;
                                dialog.dismiss();
                            }
                        })
                .show();

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timer:
                mTimerEnabled = !mTimerEnabled;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean(TIMER_ENABLED, mTimerEnabled).apply();
                supportInvalidateOptionsMenu();
                if (mTimerEnabled && !target.hasPointsSet()) {
                    openTimer();
                }
                return true;
            //case R.id.action_switch_mode:
            //    mMode = !mMode;
            //    target.switchMode(mMode, true);
            //    prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //    prefs.edit().putBoolean(TARGET_MODE, mMode).apply();
            //    supportInvalidateOptionsMenu();
            //    return true;
            case R.id.action_show_all:
                mShowAllMode = !mShowAllMode;
                target.showAll(mShowAllMode);
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean(SHOW_ALL_MODE, mShowAllMode).apply();
                supportInvalidateOptionsMenu();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openTimer() {
        startActivity(new Intent(this, SimpleFragmentActivity.TimerActivity.class));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPasse", curPasse);
        target.saveState(outState);
    }

    @Override
    public long onTargetSet(Passe passe, boolean remote) {
        passe.sort();

        passe.roundId = mRound.getId();

        db.update(passe);

        if (curPasse > savedPasses || remote) {
            savedPasses++;
            manager.sendMessage(buildInfo());
            if (remote) {
                curPasse = savedPasses + 1;
            }
        } else if (curPasse == savedPasses) {
            manager.sendMessage(buildInfo());
        }
        runOnUiThread(() -> updatePasse());
        return passe.getId();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private NotificationInfo buildInfo() {
        String title = getString(R.string.passe_n, savedPasses);
        String text = "";

        // Initialize message text
        Passe lastPasse = db.getPasse(mRound.getId(), savedPasses);
        if (lastPasse != null) {
            for (Shot shot : lastPasse.shot) {
                text += template.target.zoneToString(shot.zone, 0) + " ";
            }
            text += "\n";
        } else {
            title = getString(R.string.app_name);
        }

        // Load bow settings
        if (training.bow > 0) {
            text += template.distance.toString(this) + ": " + db.getSetting(training.bow, template.distance);
        }
        return new NotificationInfo(mRound, title, text);
    }
}
