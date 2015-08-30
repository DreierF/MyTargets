/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.views.TargetView;

public class InputActivity extends AppCompatActivity implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String SHOW_ALL_MODE = "show_all";
    private TargetView target;
    private Button next, prev;
    private int curPasse = 0;
    private int savedPasses = 0;
    private Round mRound;
    private DatabaseManager db;
    private boolean mShowAllMode = false;
    private WearMessageManager manager;
    private Training training;
    private RoundTemplate template;
    private boolean mExitOnFinish;
    private ArrayList<Round> rounds;

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

        long roundId = intent.getLongExtra(ROUND_ID, -1);
        curPasse = intent.getIntExtra(PASSE_IND, -1);
        mRound = db.getRound(roundId);
        template = mRound.info;
        training = db.getTraining(mRound.training);
        rounds = db.getRounds(mRound.training);
        savedPasses = db.getPassesOfRound(roundId).size();
        mExitOnFinish = savedPasses <= curPasse;

        target.setRoundTemplate(template);
        if (training.arrowNumbering) {
            target.setArrowNumbers(db.getArrowNumbers(training.arrow));
        }
        mShowAllMode = prefs.getBoolean(SHOW_ALL_MODE, false);
        target.showAll(mShowAllMode);

        // Send message to wearable app, that we are starting a passe
        new Thread(this::startWearNotification).start();

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
        NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, info);
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
        menu.findItem(R.id.action_show_all).setIcon(
                mShowAllMode ? R.drawable.ic_visibility_white_24dp :
                        R.drawable.ic_visibility_off_white_24dp);
        return true;
    }

    private void setPasse(int passe) {
        if (passe >= template.passes) {
            if (rounds.size() > template.index + 1) {
                mRound = rounds.get(template.index + 1);
                template = mRound.info;
                passe = 0;
                savedPasses = db.getPassesOfRound(mRound.getId()).size();
            } else if (mExitOnFinish) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        } else if (passe == -1 && template.index > 0) {
            mRound = rounds.get(template.index - 1);
            template = mRound.info;
            passe = template.passes;
        }
        if (passe < savedPasses) {
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
            if (training.timePerPasse > 0) {
                openTimer();
            }
        }
        ArrayList<Passe> oldOnes = db.getPasses(training.getId());
        target.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
    }

    private void updatePasse() {
        prev.setEnabled(curPasse > 0 || template.index > 0);
        next.setEnabled(curPasse < savedPasses && curPasse + 1 < template.passes ||
                rounds.size() > template.index + 1);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.passe) + " " + (curPasse + 1));
        getSupportActionBar()
                .setSubtitle(getString(R.string.round) + " " + (mRound.info.index + 1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all:
                mShowAllMode = !mShowAllMode;
                target.showAll(mShowAllMode);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        Intent intent = new Intent(this, SimpleFragmentActivity.TimerActivity.class);
        intent.putExtra(TimerFragment.SHOOTING_TIME, training.timePerPasse);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPasse", curPasse);
        target.saveState(outState);
    }

    @Override
    public long onTargetSet(Passe passe, boolean remote) {
        if (passe.getId() == -1) {
            mExitOnFinish = true;
        }

        // Only sort shots when all arrows are on one target face
        if (template.target.getFaceCount() == 1) {
            passe.sort();
        }

        passe.roundId = mRound.getId();
        db.update(passe);

        if (curPasse >= savedPasses || remote) {
            savedPasses++;
            manager.sendMessageUpdate(buildInfo());
            if (remote) {
                curPasse = savedPasses + 1;
            }
        } else if (curPasse + 1 == savedPasses) {
            manager.sendMessageUpdate(buildInfo());
        }
        runOnUiThread(this::updatePasse);
        return passe.getId();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private NotificationInfo buildInfo() {
        String title = getString(R.string.passe) + " " + (savedPasses + 1);
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
            text += template.distance.toString(this) + ": " +
                    db.getSetting(training.bow, template.distance);
        }
        return new NotificationInfo(mRound, title, text);
    }
}
