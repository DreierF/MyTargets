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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.managers.dao.ArrowNumberDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.RoundTemplateDataSource;
import de.dreier.mytargets.managers.dao.SightSettingDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.views.TargetView;

public class InputActivity extends AppCompatActivity implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String SHOW_ALL_MODE = "show_all";

    @Bind(R.id.targetView)
    TargetView targetView;

    @Bind(R.id.next)
    Button next;

    @Bind(R.id.prev)
    Button prev;

    private int curPasse = 0;
    private int savedPasses = 0;
    private Round round;
    private boolean mShowAllMode = false;
    private WearMessageManager manager;
    private Training training;
    private RoundTemplate template;
    private boolean mExitOnFinish;
    private ArrayList<Round> rounds;
    private PasseDataSource passeDataSource;
    private StandardRound standardRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ButterKnife.bind(this);

        targetView.setOnTargetSetListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RoundDataSource roundDataSource = new RoundDataSource(getApplicationContext());
        passeDataSource = new PasseDataSource(getApplicationContext());

        Intent intent = getIntent();
        assert intent != null;

        long roundId = intent.getLongExtra(ROUND_ID, -1);
        curPasse = intent.getIntExtra(PASSE_IND, -1);
        round = roundDataSource.get(roundId);
        template = round.info;
        training = new TrainingDataSource(this).get(round.training);
        standardRound = new StandardRoundDataSource(this).get(training.standardRoundId);
        rounds = roundDataSource.getAll(round.training);
        savedPasses = passeDataSource.getAllByRound(roundId).size();
        mExitOnFinish = savedPasses <= curPasse && standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE;

        targetView.setRoundTemplate(template);
        if (training.arrowNumbering) {
            targetView.setArrowNumbers(new ArrowNumberDataSource(getApplicationContext()).getAll(training.arrow));
        }
        mShowAllMode = prefs.getBoolean(SHOW_ALL_MODE, false);

        targetView.showAll(mShowAllMode);
        targetView.setZoneSelectionMode(!training.exact);

        // Send message to wearable app, that we are starting a passe
        if (!training.exact) {
            new Thread(this::startWearNotification).start();
        }

        if (savedInstanceState != null) {
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
        ButterKnife.unbind(this);
        if (manager != null) {
            manager.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passe, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem eye = menu.findItem(R.id.action_show_all);
        eye.setVisible(!targetView.getInputMode());
        eye.setIcon(mShowAllMode ? R.drawable.ic_visibility_white_24dp :
                R.drawable.ic_visibility_off_white_24dp);
        return true;
    }

    private void setPasse(int passe) {
        if (passe >= template.passes) {
            if (rounds.size() > template.index + 1) {
                round = rounds.get(template.index + 1);
                template = round.info;
                passe = 0;
                savedPasses = passeDataSource.getAllByRound(round.getId()).size();
            } else if (mExitOnFinish) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        } else if (passe == -1 && template.index > 0) {
            round = rounds.get(template.index - 1);
            template = round.info;
            passe = template.passes - 1;
            savedPasses = template.passes;
        }
        if (passe < savedPasses) {
            Passe p = passeDataSource.get(round.getId(), passe);
            if (p != null) {
                targetView.setPasse(p);
            } else {
                targetView.reset();
            }
        } else {
            if (passe != curPasse) {
                targetView.reset();
            }
            if (training.timePerPasse > 0) {
                openTimer();
            }
        }
        ArrayList<Passe> oldOnes = passeDataSource.getAllByTraining(training.getId());
        targetView.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
    }

    private void updatePasse() {
        prev.setEnabled(curPasse > 0 || template.index > 0);
        next.setEnabled(curPasse < savedPasses && curPasse + 1 < template.passes ||
                rounds.size() > template.index + 1 || standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.passe) + " " + (curPasse + 1));
        getSupportActionBar()
                .setSubtitle(getString(R.string.round) + " " + (round.info.index + 1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all:
                mShowAllMode = !mShowAllMode;
                targetView.showAll(mShowAllMode);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean(SHOW_ALL_MODE, mShowAllMode).apply();
                supportInvalidateOptionsMenu();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    }

    @Override
    public long onTargetSet(Passe passe, boolean remote) {
        if (passe.getId() == -1) {
            mExitOnFinish = true;
        }

        // Only sort shots when all arrows are on one target face
        if (template.target.getModel().getFaceCount() == 1) {
            passe.sort();
        }

        // Change round template if passe is out of range defined in template
        if (standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE) {
            if (template.passes <= curPasse) {
                new RoundTemplateDataSource(this).addPasse(template);
            }
        }

        passe.roundId = round.getId();
        passeDataSource.update(passe);

        if (curPasse >= savedPasses || remote) {
            savedPasses++;
            if (manager != null) {
                manager.sendMessageUpdate(buildInfo());
            }
            if (remote) {
                curPasse = savedPasses;
            }
        } else if (curPasse + 1 == savedPasses && manager != null) {
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
        Passe lastPasse = passeDataSource.get(round.getId(), savedPasses);
        if (lastPasse != null) {
            for (Shot shot : lastPasse.shot) {
                text += template.target.zoneToString(shot.zone, 0) + " ";
            }
            text += "\n";
        } else {
            title = getString(R.string.my_targets);
        }

        // Load bow settings
        if (training.bow > 0) {
            text += template.distance.toString(this) + ": " +
                    new SightSettingDataSource(getApplicationContext()).get(training.bow,
                            template.distance);
        }
        return new NotificationInfo(round, title, text);
    }
}
