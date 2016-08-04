/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightSetting;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.Icepick;
import icepick.State;

public class InputActivity extends ChildActivityBase implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";

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
    private List<Round> rounds;
    private StandardRound standardRound;

    private ActivityInputBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input);

        binding.targetView.setOnTargetSetListener(this);

        Intent intent = getIntent();
        assert intent != null;

        long roundId = intent.getLongExtra(ROUND_ID, -1);
        curPasse = intent.getIntExtra(PASSE_IND, -1);
        round = Round.get(roundId);
        template = round.info;
        training = Training.get(round.trainingId);
        standardRound = StandardRound.get(training.standardRoundId);
        rounds = training.getRounds();
        savedPasses = round.getPasses().size();

        binding.targetView.setRoundTemplate(template);
        if (training.arrowNumbering) {
            binding.targetView.setArrowNumbers(Arrow.get(training.arrow).getArrowNumbers());
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

    private void startWearNotification() {
        NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, info);
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
        if (passe >= template.endCount) {
            if (rounds.size() > template.index + 1) {
                // Go to next round if current round is finished
                round = rounds.get(template.index + 1);
                template = round.info;
                passe = 0;
                savedPasses = round.getPasses().size();
            } else if (savedPasses <= curPasse && standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
                // If standard round is over exit the input activity
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return;
            }
        } else if (passe == -1 && template.index > 0) {
            // If we navigate backwards and go past the beginning of lets say round 2
            // -> go to the last end of round 1
            round = rounds.get(template.index - 1);
            template = round.info;
            passe = template.endCount - 1;
            savedPasses = template.endCount;
        }
        if (passe < savedPasses) {
            // If the end is already saved load it from the database
            Passe p = round.getPasses().get(passe);
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
        List<Passe> oldOnes = Stream.of(training.getRounds())
                .flatMap(r -> Stream.of(r.getPasses()))
                .collect(Collectors.toList());
        binding.targetView.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
        supportInvalidateOptionsMenu();
    }

    private void updatePasse() {
        binding.prev.setEnabled(curPasse > 0 || template.index > 0);
        binding.next.setEnabled(curPasse < savedPasses &&
                (curPasse + 1 < template.endCount || // The current round is not finished
                        rounds.size() > template.index + 1 || // We still have another round
                        standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE)); // or we don't have an exit condition

        ToolbarUtils.setTitle(this, getString(R.string.passe) + " " + (curPasse + 1));
        ToolbarUtils.setSubtitle(this, getString(R.string.round) + " " + (round.info.index + 1));
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
        Intent intent = new Intent(this, SimpleFragmentActivityBase.TimerActivity.class);
        intent.putExtra(TimerFragment.SHOOTING_TIME, training.timePerPasse);
        startActivity(intent);
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
            template.addPasse();
        }

        // Change round template if passe is out of range defined in template
        if (standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE && template.endCount <= curPasse) {
            template.endCount++;
            template.update();
        }

        passe.roundId = round.getId();
        passe.save();

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

    private NotificationInfo buildInfo() {
        String title = getString(R.string.passe) + " " + (savedPasses + 1);
        String text = "";

        // Initialize message text
        Passe lastPasse = round.getPasses().get(savedPasses);
        if (lastPasse != null) {
            for (Shot shot : lastPasse.getShots()) {
                text += template.target.zoneToString(shot.zone, 0) + " ";
            }
            text += "\n";
        } else {
            title = getString(R.string.my_targets);
        }

        // Load bow settings
        if (training.bow > 0) {
            SightSetting sightSetting = Bow.get(training.bow).getSightSetting(template.distance);
            if (sightSetting != null) {
                text += String.format("%s: %s", template.distance, sightSetting.value);
            }
        }
        return new NotificationInfo(round, title, text);
    }
}
