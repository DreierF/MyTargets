/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.joda.time.DateTime;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.ArrowNumber;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightSetting;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.transitions.FabTransform;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import de.dreier.mytargets.views.TargetView;
import icepick.Icepick;
import icepick.State;

public class InputActivity extends ChildActivityBase
        implements TargetViewBase.OnEndFinishedListener, TargetView.OnEndUpdatedListener,
        LoaderManager.LoaderCallbacks<InputActivity.LoaderResult> {

    static final String TRAINING_ID = "training_id";
    static final String ROUND_ID = "round_id";
    static final String END_INDEX = "end_ind";

    @State(ParcelsBundler.class)
    LoaderResult data;

    private WearMessageManager manager;
    private ActivityInputBinding binding;
    private boolean transitionFinished = true;
    private EShowMode showMode = EShowMode.END;
    private TargetView targetView;

    @NonNull
    public static IntentWrapper createIntent(Round round) {
        return getIntent(round, 0);
    }

    public static IntentWrapper getIntent(Round round, int endIndex) {
        return new IntentWrapper(InputActivity.class)
                .with(TRAINING_ID, round.trainingId)
                .with(ROUND_ID, round.getId())
                .with(END_INDEX, endIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input);

        setSupportActionBar(binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        FabTransformUtil.setup(this, binding.getRoot());

        if (Utils.isLollipop()) {
            setupTransitionListener();
        }

        showMode = SettingsManager.getShowMode();

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (data != null) {
            onDataLoadFinished();
            updateEnd();
        } else {
            getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this).forceLoad();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitionListener() {
        final Transition sharedElementEnterTransition = getWindow()
                .getSharedElementEnterTransition();
        if (sharedElementEnterTransition != null && sharedElementEnterTransition instanceof FabTransform) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_end, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem eye = menu.findItem(R.id.action_show);
        final MenuItem showSidebar = menu.findItem(R.id.action_show_sidebar);
        final MenuItem grouping = menu.findItem(R.id.action_grouping);
        if (targetView == null) {
            eye.setVisible(false);
            showSidebar.setVisible(false);
            grouping.setVisible(false);
        } else {
            final boolean plotting = targetView.getInputMode() == EInputMethod.PLOTTING;
            eye.setVisible(plotting);
            grouping.setVisible(plotting);
            showSidebar.setIcon(
                    plotting ? R.drawable.ic_keyboard_white_24dp : R.drawable.ic_keyboard_white_off_24dp);
            showSidebar.setVisible(getCurrentEnd() != null ? getCurrentEnd().getId() == 0 : false);
        }

        menu.findItem(SettingsManager.getShowMode().actionItemId).setChecked(true);
        switch (SettingsManager.getAggregationStrategy()) {
            case NONE:
                menu.findItem(R.id.action_grouping_none).setChecked(true);
                break;
            case AVERAGE:
                menu.findItem(R.id.action_grouping_average).setChecked(true);
                break;
            case CLUSTER:
                menu.findItem(R.id.action_grouping_cluster).setChecked(true);
                break;
            default:
                // Never called: All enum values are checked
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grouping_none:
                targetView.setAggregationStrategy(EAggregationStrategy.NONE);
                break;
            case R.id.action_grouping_average:
                targetView.setAggregationStrategy(EAggregationStrategy.AVERAGE);
                break;
            case R.id.action_grouping_cluster:
                targetView.setAggregationStrategy(EAggregationStrategy.CLUSTER);
                break;
            case R.id.action_show_end:
                setShowMode(EShowMode.END);
                break;
            case R.id.action_show_round:
                setShowMode(EShowMode.ROUND);
                break;
            case R.id.action_show_training:
                setShowMode(EShowMode.TRAINING);
                break;
            case R.id.action_show_sidebar:
                final EInputMethod inputMethod = targetView.getInputMode() == EInputMethod.KEYBOARD
                        ? EInputMethod.PLOTTING
                        : EInputMethod.KEYBOARD;
                targetView.setInputMethod(inputMethod, true);
                SettingsManager.setInputMethod(inputMethod);
                supportInvalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        item.setChecked(true);
        return true;
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        long trainingId = args.getLong(TRAINING_ID);
        long roundId = args.getLong(ROUND_ID);
        int endIndex = args.getInt(END_INDEX);
        return new UITaskAsyncTaskLoader(this, trainingId, roundId, endIndex);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult> loader, LoaderResult data) {
        this.data = data;
        onDataLoadFinished();
        showEnd(data.endIndex);
    }

    private void onDataLoadFinished() {
        setTitle(data.training.title);
        if (!binding.targetViewStub.isInflated()) {
            binding.targetViewStub.getViewStub().inflate();
        }
        targetView = (TargetView) binding.targetViewStub.getBinding().getRoot();
        targetView.setTarget(getCurrentRound().getTarget());
        targetView.setArrow(data.arrowDiameter, data.arrowNumbers);
        targetView.setOnTargetSetListener(InputActivity.this);
        targetView.setUpdateListener(InputActivity.this);
        targetView.reloadSettings();
        targetView.setAggregationStrategy(SettingsManager.getAggregationStrategy());
        targetView.setInputMethod(SettingsManager.getInputMethod(), false);
        updateOldShoots();

        binding.next.setOnClickListener(view -> showEnd(data.endIndex + 1));
        binding.prev.setOnClickListener(view -> showEnd(data.endIndex - 1));

        // Send message to wearable app, that we are starting an end
        new Thread(InputActivity.this::startWearNotification).start();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {

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

    private void showEnd(int endIndex) {
        // Create a new end
        if (endIndex == getEnds().size()) {
            Passe end = new Passe(getTemplate().arrowsPerEnd);
            end.roundId = getCurrentRound().getId();
            getEnds().add(end);
            updateOldShoots();
        }

        data.endIndex = endIndex;

        // Open timer if end has not been saved yet
        if (getCurrentEnd().getId() == 0 && data.training.timePerPasse > 0) {
            openTimer();
        }
        updateEnd();
        supportInvalidateOptionsMenu();
    }

    public void updateOldShoots() {
        final LoaderResult data = this.data;
        final Stream<Shot> shotStream = Stream
                .zip(data.rounds.iterator(), data.ends.iterator(), Pair::new)
                .filter(p -> shouldShowRound(p.first))
                .flatMap(p -> Stream.of(p.second))
                .filter(this::shouldShowEnd)
                .flatMap(p -> Stream.of(p.shots));
        targetView.setTransparentShots(shotStream);
    }

    private boolean shouldShowRound(Round r) {
        return showMode != EShowMode.END
                && (showMode == EShowMode.TRAINING || r.getId() == getCurrentEnd().roundId);
    }

    private boolean shouldShowEnd(Passe p) {
        return p.getId() != getCurrentEnd().getId() && p.exact;
    }

    private void updateEnd() {
        targetView.setEnd(getCurrentEnd());
        binding.endTitle.setText(getString(R.string.passe) + " " + (data.endIndex + 1));
        binding.roundTitle.setText(getString(R.string.round) + " " + (getTemplate().index + 1));
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        binding.prev.setEnabled(data.endIndex > 0);
        binding.next.setEnabled(getCurrentEnd().getId() != 0 &&
                (data.endIndex + 1 < getTemplate().endCount || // The current round is not finished
                        data.standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE)); // or we don't have an exit condition
    }

    public void setShowMode(EShowMode showMode) {
        this.showMode = showMode;
        SettingsManager.setShowMode(showMode);
        updateOldShoots();
    }

    private void openTimer() {
        if (transitionFinished) {
            TimerFragment.getIntent(data.training.timePerPasse)
                    .withContext(this)
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
                TimerFragment.getIntent(data.training.timePerPasse)
                        .withContext(InputActivity.this)
                        .start();
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
    public void onEndUpdated(List<Shot> changedEnd) {
        getCurrentEnd().shots = changedEnd;

        // Set current end score
        int reachedEndPoints = getCurrentEnd().getReachedPoints(getCurrentRound().getTarget());
        int maxEndPoints = getCurrentRound().getTarget().getEndMaxPoints(getTemplate().arrowsPerEnd);
        binding.scoreEnd.setText(reachedEndPoints + "/" + maxEndPoints);

        // Set current round score
        int reachedRoundPoints = Stream.of(getEnds())
                .reduce(0, (sum, end) -> sum + end.getReachedPoints(getCurrentRound().getTarget()));
        int maxRoundPoints = maxEndPoints * getEnds().size();
        binding.scoreRound.setText(reachedRoundPoints + "/" + maxRoundPoints);
    }

    @Override
    public void onEndFinished(List<Shot> shots, boolean remote) {
        getCurrentEnd().shots = shots;
        getCurrentEnd().exact = targetView.getInputMode() == EInputMethod.PLOTTING;
        if (getCurrentEnd().getId() == 0) {
            getCurrentEnd().saveDate = new DateTime();
        }

        // Change round template if end is out of range defined in template
        if (data.standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE && getTemplate().endCount - 1 == data.endIndex) {
            getTemplate().addEnd();
        }

        getCurrentEnd().save();

        if (manager != null) {
            manager.sendMessageUpdate(buildInfo());
        }
        if (remote) {
            showEnd(getEnds().size());
        }
        updateNavigationButtons();
        supportInvalidateOptionsMenu();
    }

    private NotificationInfo buildInfo() {
        String title = getString(R.string.passe) + " " + (getEnds().size());
        String text = "";

        // Initialize message text
        if (getEnds().size() > 0) {
            Passe lastEnd = lastItem(getEnds());
            for (Shot shot : lastEnd.shots) {
                text += getCurrentRound().getTarget().zoneToString(shot.zone, shot.index) + " ";
            }
            text += "\n";
        } else {
            title = getString(R.string.my_targets);
        }

        // Load bow settings
        if (data.training.bow > 0) {
            final SightSetting sightSetting = Bow.get(data.training.bow).getSightSetting(getTemplate().distance);
            if (sightSetting != null) {
                text += String.format("%s: %s", getTemplate().distance, sightSetting.value);
            }
        }
        return new NotificationInfo(getCurrentRound(), title, text);
    }

    private List<Passe> getEnds() {
        if (data.ends == null) {
            return null;
        }
        return data.ends.get(data.roundIndex);
    }

    private Round getCurrentRound() {
        return data.rounds.get(data.roundIndex);
    }

    private RoundTemplate getTemplate() {
        return getCurrentRound().info;
    }

    private Passe getCurrentEnd() {
        final List<Passe> ends = getEnds();
        if (ends == null) {
            return null;
        }
        return ends.get(data.endIndex);
    }

    private static <T> T lastItem(List<T> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
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

    private static class UITaskAsyncTaskLoader extends AsyncTaskLoader<LoaderResult> {
        private final long trainingId;
        private final long roundId;
        private final int endIndex;

        public UITaskAsyncTaskLoader(Context context, long trainingId, long roundId, int endIndex) {
            super(context);
            this.trainingId = trainingId;
            this.roundId = roundId;
            this.endIndex = endIndex;
        }

        @Override
        public LoaderResult loadInBackground() {
            LoaderResult result = new LoaderResult();
            result.endIndex = endIndex;
            result.training = Training.get(trainingId);
            result.rounds = new ArrayList<>(result.training.getRounds());
            result.roundIndex = 0;
            for (int i = 0; i < result.rounds.size(); i++) {
                if (result.rounds.get(i).getId() == roundId) {
                    result.roundIndex = i;
                }
            }
            result.ends = new ArrayList<>(Stream.of(result.rounds)
                    .map(r -> new ArrayList<>(r.getPasses()))
                    .collect(Collectors.toList()));

            result.standardRound = result.training.getStandardRound();
            result.arrowDiameter = new Dimension(5, Dimension.Unit.MILLIMETER);
            if (result.training.arrow > 0) {
                Arrow arrow = result.training.getArrow();
                if (arrow != null) {
                    result.arrowDiameter = arrow.diameter;
                }
            }

            result.arrowNumbers = result.training.arrowNumbering
                    ? result.training.getArrow().getArrowNumbers()
                    : Collections.emptyList();

            return result;
        }
    }

    @Parcel
    static class LoaderResult {
        Training training;
        ArrayList<Round> rounds;
        ArrayList<ArrayList<Passe>> ends;
        StandardRound standardRound;
        int roundIndex;
        Dimension arrowDiameter;
        List<ArrowNumber> arrowNumbers;
        int endIndex;
    }
}
