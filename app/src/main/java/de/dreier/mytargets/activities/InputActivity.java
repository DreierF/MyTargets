/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.interfaces.OnEndUpdatedListener;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.managers.dao.ArrowNumberDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.RoundTemplateDataSource;
import de.dreier.mytargets.managers.dao.SightSettingDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import icepick.Icepick;
import icepick.State;

public class InputActivity extends ChildActivityBase implements OnTargetSetListener, OnEndUpdatedListener {

    private static final String ROUND_ID = "round_id";
    private static final String PASSE_IND = "passe_ind";

    /**
     * Zero-based index of the currently displayed end.
     */
    @State
    int curPasse = 0;

    private int savedPasses = 0;
    private Round round;
    private WearMessageManager manager;
    private Training training;
    private RoundTemplate template;
    private PasseDataSource passeDataSource;
    private StandardRound standardRound;

    private ActivityInputBinding binding;
    private boolean transitionFinished = true;

    @NonNull
    public static IntentWrapper newEndIntent(Activity activity, long roundId) {
        return getEndIntent(activity, roundId, 0);
    }

    public static IntentWrapper getEndIntent(Activity activity, long roundId, int passeIndex) {
        Intent i = new Intent(activity, InputActivity.class);
        i.putExtra(ROUND_ID, roundId);
        i.putExtra(PASSE_IND, passeIndex);
        return new IntentWrapper(activity, i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input);

        setSupportActionBar(binding.toolbar);
        FabTransformUtil.setup(this, binding.getRoot());

        if (Utils.isLollipop()) {
            setupTransitionListener();
        }

        binding.targetView.setOnTargetSetListener(this);
        binding.targetView.setUpdateListener(this);

        RoundDataSource roundDataSource = new RoundDataSource();
        passeDataSource = new PasseDataSource();

        Intent intent = getIntent();
        assert intent != null;

        long roundId = intent.getLongExtra(ROUND_ID, -1);
        curPasse = intent.getIntExtra(PASSE_IND, -1);
        round = roundDataSource.get(roundId);
        template = round.info;
        training = new TrainingDataSource().get(round.trainingId);
        standardRound = new StandardRoundDataSource().get(training.standardRoundId);
        savedPasses = passeDataSource.getAllByRound(roundId).size();

        setTitle(training.title);

        binding.targetView.setRoundTemplate(template);
        Dimension diameter = new Dimension(5, Dimension.Unit.MILLIMETER);
        if (training.arrow > 0) {
            Arrow arrow = new ArrowDataSource().get(training.arrow);
            if (arrow != null) {
                diameter = arrow.diameter;
            }
        }
        binding.targetView.setArrowDiameter(diameter);
        if (training.arrowNumbering) {
            binding.targetView.setArrowNumbers(new ArrowNumberDataSource().getAll(training.arrow));
        }

        // Send message to wearable app, that we are starting a end
        new Thread(this::startWearNotification).start();

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState != null) {
            updatePasse();
        } else {
            setPasse(curPasse);
        }

        binding.next.setOnClickListener(view -> setPasse(curPasse + 1));
        binding.prev.setOnClickListener(view -> setPasse(curPasse - 1));

        ToolbarUtils.showHomeAsUp(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitionListener() {
        if (getWindow().getSharedElementEnterTransition() != null) {
            transitionFinished = false;
            getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    transitionFinished = true;
                    getWindow().getSharedElementEnterTransition().removeListener(this);
                }
            });
        }
    }

    private void startWearNotification() {
        NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.targetView.reloadSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        eye.setVisible(!binding.targetView.getInputMode());
        final MenuItem showSidebar = menu.findItem(R.id.action_show_sidebar);
        showSidebar.setIcon(binding.targetView.getInputMode() ? R.drawable.ic_album_24dp :
                R.drawable.ic_grain_24dp);
        showSidebar.setVisible(curPasse >= savedPasses);
        switch (SettingsManager.getShowMode()) {
            case END:
                menu.findItem(R.id.action_show_end).setChecked(true);
                break;
            case ROUND:
                menu.findItem(R.id.action_show_round).setChecked(true);
                break;
            case TRAINING:
                menu.findItem(R.id.action_show_training).setChecked(true);
                break;
        }
        return true;
    }

    private void setPasse(int passe) {
        if (passe >= template.endCount && savedPasses <= curPasse && standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
            // If standard round is over exit the input activity
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return;
        }
        if (passe < savedPasses) {
            // If the end is already saved load it from the database
            Passe p = passeDataSource.get(round.getId(), passe);
            if (p != null) {
                binding.targetView.setPasse(p);
            } else {
                binding.targetView.reset();
                binding.targetView.setRoundId(round.getId());
            }
        } else {
            // otherwise create a new one
            if (passe != curPasse) {
                binding.targetView.reset();
                binding.targetView.setRoundId(round.getId());
            }
            if (training.timePerPasse > 0) {
                openTimer();
            }
        }
        ArrayList<Passe> oldOnes = passeDataSource.getAllByTraining(training.getId());
        binding.targetView.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
        supportInvalidateOptionsMenu();
    }

    private void updatePasse() {
        binding.prev.setEnabled(curPasse > 0);
        binding.next.setEnabled(curPasse < savedPasses &&
                (curPasse + 1 < template.endCount || // The current round is not finished
                        standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE)); // or we don't have an exit condition

        binding.endTitle.setText(getString(R.string.passe) + " " + (curPasse + 1));
        binding.roundTitle.setText(getString(R.string.round) + " " + (round.info.index + 1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_end:
                item.setChecked(true);
                binding.targetView.setShowMode(EShowMode.END);
                return true;
            case R.id.action_show_round:
                item.setChecked(true);
                binding.targetView.setShowMode(EShowMode.ROUND);
                return true;
            case R.id.action_show_training:
                item.setChecked(true);
                binding.targetView.setShowMode(EShowMode.TRAINING);
                return true;
            case R.id.action_show_sidebar:
                binding.targetView.switchMode(!binding.targetView.getInputMode(), true);
                supportInvalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openTimer() {
        if (transitionFinished) {
            TimerFragment.getIntent(this, training.timePerPasse)
                    .start();
        } else if (Utils.isLollipop()) {
            startTimerDelayed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startTimerDelayed() {
        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                TimerFragment.getIntent(InputActivity.this, training.timePerPasse).start();
                getWindow().getSharedElementEnterTransition().removeListener(this);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public long onTargetSet(Passe passe, boolean remote) {
        // Change round template if end is out of range defined in template
        if (standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE && template.endCount <= curPasse) {
            new RoundTemplateDataSource().addPasse(template);
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
    public void onEndUpdated(Passe p, List<Passe> old) {
        int reachedEndPoints = p.getReachedPoints(template.target);
        int maxEndPoints = round.info.target.getEndMaxPoints(round.info.arrowsPerEnd);
        binding.scoreEnd.setText(reachedEndPoints + "/" + maxEndPoints);
        final List<Passe> ends = Stream.of(old)
                .filter(p2 -> round.getId() == p2.roundId && p2.getId() != p.getId())
                .collect(Collectors.toList());
        ends.add(p);
        int reachedRoundPoints = Stream.of(ends)
                .reduce(0, (sum, end) -> sum + end.getReachedPoints(round.info.target));
        int maxRoundPoints = maxEndPoints * ends.size();
        binding.scoreRound.setText(reachedRoundPoints + "/" + maxRoundPoints);
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
            final SightSetting sightSetting = new SightSettingDataSource().get(training.bow,
                    template.distance);
            if (sightSetting != null) {
                text += String.format("%s: %s", template.distance, sightSetting.value);
            }
        }
        return new NotificationInfo(round, title, text);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static abstract class TransitionAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }
}
