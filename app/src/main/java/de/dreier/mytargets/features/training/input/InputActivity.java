/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.features.training.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Stream;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.features.rounds.EditRoundFragment;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.timer.TimerFragment;
import de.dreier.mytargets.features.training.RoundFragment;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.SharedUtils;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.transitions.FabTransform;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import de.dreier.mytargets.utils.transitions.TransitionAdapter;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InputActivity extends ChildActivityBase
        implements TargetViewBase.OnEndFinishedListener, TargetView.OnEndUpdatedListener,
        LoaderManager.LoaderCallbacks<InputActivity.LoaderResult> {

    static final String TRAINING_ID = "training_id";
    static final String ROUND_ID = "round_id";
    static final String END_INDEX = "end_ind";

    @State(ParcelsBundler.class)
    LoaderResult data;

    private ActivityInputBinding binding;
    private boolean transitionFinished = true;
    private ETrainingScope shotShowScope = ETrainingScope.END;
    private ETrainingScope summaryShowScope = null;
    private TargetView targetView;

    private BroadcastReceiver updateReceiver = new MobileWearableClient.EndUpdateReceiver() {

        @Override
        protected void onUpdate(Long trainingId, Long roundId, End end) {
            Bundle extras = getIntent().getExtras();
            extras.putLong(TRAINING_ID, trainingId);
            extras.putLong(ROUND_ID, roundId);
            extras.putInt(END_INDEX, end.index);
            getSupportLoaderManager().restartLoader(0, extras, InputActivity.this).forceLoad();
        }
    };

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

    private static boolean shouldShowRound(Round r, ETrainingScope shotShowScope, Long roundId) {
        return shotShowScope != ETrainingScope.END
                && (shotShowScope == ETrainingScope.TRAINING || r.getId().equals(roundId));
    }

    private static boolean shouldShowEnd(End end, Long currentEndId) {
        return !SharedUtils.equals(end.getId(), currentEndId) && end.exact;
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

        shotShowScope = SettingsManager.getShowMode();

        updateSummaryVisibility();

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (data != null) {
            onDataLoadFinished();
            updateEnd();
        } else {
            getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this).forceLoad();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    private void updateSummaryVisibility() {
        SummaryConfiguration config = SettingsManager.getInputSummaryConfiguration();
        binding.endSummary.setVisibility(config.showEnd ? VISIBLE : GONE);
        binding.roundSummary.setVisibility(config.showRound ? VISIBLE : GONE);
        binding.trainingSummary.setVisibility(config.showTraining ? VISIBLE : GONE);
        binding.averageSummary.setVisibility(config.showAverage ? VISIBLE : GONE);
        summaryShowScope = config.averageScope;
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
        final MenuItem keyboard = menu.findItem(R.id.action_keyboard);
        final MenuItem grouping = menu.findItem(R.id.action_grouping);
        final MenuItem timer = menu.findItem(R.id.action_timer);
        final MenuItem newRound = menu.findItem(R.id.action_new_round);
        if (targetView == null || data.getEnds().size() == 0) {
            eye.setVisible(false);
            keyboard.setVisible(false);
            grouping.setVisible(false);
            timer.setVisible(false);
            newRound.setVisible(false);
        } else {
            final boolean plotting = targetView.getInputMode() == EInputMethod.PLOTTING;
            eye.setVisible(plotting);
            grouping.setVisible(plotting);
            keyboard.setIcon(plotting
                    ? R.drawable.ic_keyboard_white_24dp
                    : R.drawable.ic_keyboard_white_off_24dp);
            keyboard.setChecked(!plotting);
            keyboard.setVisible(data.getCurrentEnd().getId() == null);
            timer.setIcon(SettingsManager.getTimerEnabled()
                    ? R.drawable.ic_timer_off_white_24dp
                    : R.drawable.ic_timer_white_24dp);
            timer.setVisible(true);
            timer.setChecked(SettingsManager.getTimerEnabled());
            newRound.setVisible(data.training.standardRoundId == null);
        }

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
            default:
                // Never called: All enum values are checked
                break;
        }
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
                setShotShowScope(ETrainingScope.END);
                break;
            case R.id.action_show_round:
                setShotShowScope(ETrainingScope.ROUND);
                break;
            case R.id.action_show_training:
                setShotShowScope(ETrainingScope.TRAINING);
                break;
            case R.id.action_keyboard:
                final EInputMethod inputMethod = targetView.getInputMode() == EInputMethod.KEYBOARD
                        ? EInputMethod.PLOTTING
                        : EInputMethod.KEYBOARD;
                targetView.setInputMethod(inputMethod, true);
                SettingsManager.setInputMethod(inputMethod);
                item.setChecked(inputMethod == EInputMethod.KEYBOARD);
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_timer:
                SettingsManager.setTimerEnabled(!SettingsManager.getTimerEnabled());
                openTimer();
                item.setChecked(SettingsManager.getTimerEnabled());
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_new_round:
                EditRoundFragment.createIntent(data.training)
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        item.setChecked(true);
        return true;
    }

    private void setShotShowScope(ETrainingScope shotShowScope) {
        this.shotShowScope = shotShowScope;
        SettingsManager.setShowMode(shotShowScope);
        updateOldShoots();
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
        targetView.setTarget(data.getCurrentRound().getTarget());
        targetView.setArrow(data.arrowDiameter, data.training.arrowNumbering);
        targetView.setOnTargetSetListener(InputActivity.this);
        targetView.setUpdateListener(InputActivity.this);
        targetView.reloadSettings();
        targetView.setAggregationStrategy(SettingsManager.getAggregationStrategy());
        targetView.setInputMethod(SettingsManager.getInputMethod(), false);
        updateOldShoots();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {

    }

    private void showEnd(int endIndex) {
        // Create a new end
        data.endIndex = endIndex;
        if (endIndex >= data.getEnds().size()) {
            data.getCurrentRound().addEnd();
            updateOldShoots();
        }

        // Open timer if end has not been saved yet
        openTimer();
        updateEnd();
        supportInvalidateOptionsMenu();
    }

    public void updateOldShoots() {
        final End currentEnd = data.getCurrentEnd();
        final Long currentRoundId = data.getCurrentRound().getId();
        final Long currentEndId = currentEnd.getId();
        final ETrainingScope shotShowScope = this.shotShowScope;
        final LoaderResult data = this.data;
        final Stream<Shot> shotStream = Stream.of(data.training.getRounds())
                .filter((r) -> shouldShowRound(r, shotShowScope, currentRoundId))
                .flatMap(r -> Stream.of(r.getEnds()))
                .filter((end) -> shouldShowEnd(end, currentEndId))
                .flatMap(p -> Stream.of(p.getShots()));
        targetView.setTransparentShots(shotStream);
    }

    private void openTimer() {
        if (data.getCurrentEnd().getId() == null
                && data.getCurrentEnd().getShots().get(0).scoringRing == Shot.NOTHING_SELECTED
                && SettingsManager.getTimerEnabled()) {
            if (transitionFinished) {
                TimerFragment.getIntent()
                        .withContext(this)
                        .start();
            } else if (Utils.isLollipop()) {
                startTimerDelayed();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startTimerDelayed() {
        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                TimerFragment.getIntent()
                        .withContext(InputActivity.this)
                        .start();
                getWindow().getSharedElementEnterTransition().removeListener(this);
            }
        });
    }

    private void updateEnd() {
        targetView.setEnd(data.getCurrentEnd());
        final int totalEnds = data.getCurrentRound().maxEndCount == null
                ? data.getEnds().size()
                : data.getCurrentRound().maxEndCount;
        binding.endTitle.setText(
                getString(R.string.passe) + " " + (data.endIndex + 1) + "/" + totalEnds);
        binding.roundTitle.setText(getString(
                R.string.round) + " " + (data.getCurrentRound().index + 1) + "/" + data.training
                .getRounds().size());
        updateNavigationButtons();
        updateWearNotification();
    }

    private void updateWearNotification() {
        Timber.d("updateWearNotification()");
        ApplicationInstance.wearableClient.sendUpdateTrainingFromLocalBroadcast(data.training);
    }

    private void updateNavigationButtons() {
        updatePreviousButton();
        updateNextButton();
    }

    private void updatePreviousButton() {
        final boolean isFirstEnd = data.endIndex == 0;
        final boolean isFirstRound = data.roundIndex == 0;
        boolean showPreviousRound = isFirstEnd && !isFirstRound;
        final boolean isEnabled = !isFirstEnd || !isFirstRound;
        final int color;
        if (showPreviousRound) {
            final Round round = data.training.getRounds().get(data.roundIndex - 1);
            binding.prev.setOnClickListener(view -> openRound(round, round.getEnds().size() - 1));
            binding.prev.setText(R.string.previous_round);
            color = getResources().getColor(R.color.colorPrimary);
        } else {
            binding.prev.setOnClickListener(view -> showEnd(data.endIndex - 1));
            binding.prev.setText(R.string.prev);
            color = Color.BLACK;
        }
        binding.prev.setTextColor(Utils.argb(isEnabled ? 0xFF : 0x42, color));
        binding.prev.setEnabled(isEnabled);
    }

    private void updateNextButton() {
        final boolean endFinished = data != null && data.getCurrentEnd().getId() != null;
        boolean isLastEnd = data != null &&
                data.getCurrentRound().maxEndCount != null &&
                data.endIndex + 1 == data.getCurrentRound().maxEndCount;
        final boolean hasOneMoreRound = data != null &&
                data.roundIndex + 1 < data.training.getRounds().size();
        boolean showNextRound = isLastEnd && hasOneMoreRound;
        final boolean isEnabled = endFinished && (!isLastEnd || hasOneMoreRound);
        final int color;
        if (showNextRound) {
            final Round round = data.training.getRounds().get(data.roundIndex + 1);
            binding.next.setOnClickListener(view -> openRound(round, 0));
            binding.next.setText(R.string.next_round);
            color = getResources().getColor(R.color.colorPrimary);
        } else {
            binding.next.setOnClickListener(view -> showEnd(data.endIndex + 1));
            binding.next.setText(R.string.next);
            color = Color.BLACK;
        }
        binding.next.setTextColor(Utils.argb(isEnabled ? 0xFF : 0x42, color));
        binding.next.setEnabled(isEnabled);
    }

    private void openRound(Round round, int endIndex) {
        finish();
        RoundFragment.getIntent(round)
                .noAnimation()
                .withContext(this)
                .start();
        InputActivity.getIntent(round, endIndex)
                .withContext(this)
                .start();
    }

    @Override
    public void onEndUpdated(List<Shot> changedEnd) {
        data.getCurrentEnd().setShots(changedEnd);

        // Set current end score
        Score reachedEndScore = data.getCurrentRound().getTarget()
                .getReachedScore(data.getCurrentEnd());
        binding.endScore.setText(reachedEndScore.toString());

        // Set current round score
        Score reachedRoundScore = Stream.of(data.getEnds())
                .map(end -> data.getCurrentRound().getTarget().getReachedScore(end))
                .collect(Score.sum());
        binding.roundScore.setText(reachedRoundScore.toString());

        // Set current training score
        Score reachedTrainingScore = Stream.of(data.training.getRounds())
                .flatMap(r -> Stream.of(r.getEnds())
                        .map(end -> r.getTarget().getReachedScore(end)))
                .collect(Score.sum());
        binding.trainingScore.setText(reachedTrainingScore.toString());

        switch (summaryShowScope) {
            case END:
                binding.averageScore.setText(reachedEndScore.getShotAverageFormatted());
                break;
            case ROUND:
                binding.averageScore.setText(reachedRoundScore.getShotAverageFormatted());
                break;
            case TRAINING:
                binding.averageScore.setText(reachedTrainingScore.getShotAverageFormatted());
                break;
            default:
                break;
        }
    }

    @Override
    public void onEndFinished(List<Shot> shots) {
        data.getCurrentEnd().setShots(shots);
        data.getCurrentEnd().exact = targetView.getInputMode() == EInputMethod.PLOTTING;
        if (data.getCurrentEnd().getId() == null) {
            data.getCurrentEnd().saveTime = new DateTime();
        }
        data.getCurrentEnd().save();

        updateWearNotification();
        updateNavigationButtons();
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
            Training training = Training.get(trainingId);
            final LoaderResult result = new LoaderResult(training);
            result.setRoundId(roundId);
            result.setEndIndex(endIndex);

            if (training.arrowId != null) {
                Arrow arrow = training.getArrow();
                if (arrow != null) {
                    result.setArrowDiameter(arrow.diameter);
                }
            }
            final Bow bow = training.getBow();
            if (bow != null) {
                result.sightMark = bow.getSightSetting(result.getDistance());
            }
            return result;
        }
    }

    @Parcel
    static class LoaderResult {
        final Training training;
        StandardRound standardRound;
        Dimension arrowDiameter = new Dimension(5, Dimension.Unit.MILLIMETER);
        SightMark sightMark = null;
        int roundIndex = 0;
        int endIndex = 0;

        @ParcelConstructor
        public LoaderResult(Training training) {
            this.training = training.ensureLoaded();
            this.standardRound = training.getStandardRound();
        }

        public void setRoundId(long roundId) {
            List<Round> rounds = training.getRounds();
            roundIndex = 0;
            for (int i = 0; i < rounds.size(); i++) {
                if (rounds.get(i).getId() == roundId) {
                    roundIndex = i;
                }
            }
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = Math.min(endIndex, getCurrentRound().getEnds().size());
        }

        public Dimension getDistance() {
            return getCurrentRound().distance;
        }

        @NonNull
        private List<End> getEnds() {
            return getCurrentRound().getEnds();
        }

        @NonNull
        private Round getCurrentRound() {
            return training.getRounds().get(roundIndex);
        }

        @NonNull
        private End getCurrentEnd() {
            List<End> ends = getEnds();
            if (ends.size() <= endIndex || endIndex < 0 || ends.size() == 0) {
                endIndex = ends.size();
                getCurrentRound().addEnd();
                ends = getEnds();
            }
            return ends.get(endIndex);
        }

        public void setArrowDiameter(Dimension arrowDiameter) {
            this.arrowDiameter = arrowDiameter;
        }
    }
}
