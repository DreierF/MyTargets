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
import android.content.Intent;
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
import android.text.InputType;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.File;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.base.gallery.GalleryActivity;
import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.features.rounds.EditRoundFragment;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.timer.TimerFragment;
import de.dreier.mytargets.features.training.RoundFragment;
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
import de.dreier.mytargets.shared.utils.ImageList;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.BROADCAST_TIMER_SETTINGS_FROM_REMOTE;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;

public class InputActivity extends ChildActivityBase
        implements TargetViewBase.OnEndFinishedListener, TargetView.OnEndUpdatedListener,
        LoaderManager.LoaderCallbacks<InputActivity.LoaderResult> {

    static final String TRAINING_ID = "training_id";
    static final String ROUND_ID = "round_id";
    static final String END_INDEX = "end_ind";
    private static final int GALLERY_REQUEST_CODE = 1;

    @State(ParcelsBundler.class)
    LoaderResult data;

    private ActivityInputBinding binding;
    private boolean transitionFinished = true;
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

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            supportInvalidateOptionsMenu();
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

        updateSummaryVisibility();

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (data == null) {
            getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this).forceLoad();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE));
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver,
                new IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_REMOTE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (data != null) {
            onDataLoadFinished();
            updateEnd();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageList imageList = GalleryActivity.getResult(data);
            this.data.getCurrentEnd().images = imageList.toEndImageList();
            for (File image : imageList.getRemovedImages()) {
                image.delete();
            }
            this.data.getCurrentEnd().save();
            updateEnd();
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
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
        if (sharedElementEnterTransition != null &&
                sharedElementEnterTransition instanceof FabTransform) {
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
        final MenuItem timer = menu.findItem(R.id.action_timer);
        final MenuItem newRound = menu.findItem(R.id.action_new_round);
        final MenuItem takePicture = menu.findItem(R.id.action_photo);
        if (targetView == null || data.getEnds().size() == 0) {
            takePicture.setVisible(false);
            timer.setVisible(false);
            newRound.setVisible(false);
        } else {
            takePicture.setVisible(Utils.hasCameraHardware(this));
            timer.setIcon(SettingsManager.getTimerEnabled()
                    ? R.drawable.ic_timer_off_white_24dp
                    : R.drawable.ic_timer_white_24dp);
            timer.setVisible(true);
            timer.setChecked(SettingsManager.getTimerEnabled());
            newRound.setVisible(data.training.standardRoundId == null);
            takePicture.setVisible(Utils.hasCameraHardware(this));
            takePicture.setIcon(data.getCurrentEnd().getImages().isEmpty() ?
                    R.drawable.ic_photo_camera_white_24dp : R.drawable.ic_image_white_24dp);
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
            case R.id.action_photo:
                GalleryActivity.getIntent(data.getCurrentEnd())
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_comment:
                new MaterialDialog.Builder(this)
                        .title(R.string.comment)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("", data.getCurrentEnd().comment, (dialog, input) -> {
                            data.getCurrentEnd().comment = input.toString();
                            data.getCurrentEnd().save();
                        })
                        .negativeText(android.R.string.cancel)
                        .show();
                return true;
            case R.id.action_timer:
                boolean timerEnabled = !SettingsManager.getTimerEnabled();
                SettingsManager.setTimerEnabled(timerEnabled);
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(SettingsManager.getTimerSettings());
                openTimer();
                item.setChecked(timerEnabled);
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_settings:
                SettingsActivity.getIntent(ESettingsScreens.INPUT)
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_new_round:
                EditRoundFragment.createIntent(data.training)
            case R.id.action_photo:
                GalleryActivity.getIntent(new ImageList(data.getCurrentEnd().getImages()), getString(R.string.end_n, data.endIndex + 1))
                        .withContext(this)
                        .forResult(GALLERY_REQUEST_CODE)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            End end = data.getCurrentRound().addEnd();
            end.exact = SettingsManager.getInputMethod() == EInputMethod.PLOTTING;
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
        final ETrainingScope shotShowScope = SettingsManager.getShowMode();
        final LoaderResult data = this.data;
        final Stream<Shot> shotStream = Stream.of(data.training.getRounds())
                .filter((r) -> shouldShowRound(r, shotShowScope, currentRoundId))
                .flatMap(r -> Stream.of(r.getEnds()))
                .filter((end) -> shouldShowEnd(end, currentEndId))
                .flatMap(p -> Stream.of(p.getShots()));
        targetView.setTransparentShots(shotStream);
    }

    private void openTimer() {
        if (data.getCurrentEnd().isEmpty() && SettingsManager.getTimerEnabled()) {
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
        final boolean endFinished = data != null && !data.getCurrentEnd().isEmpty();
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
        data.getCurrentEnd().save();

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
                    break;
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
                End end = getCurrentRound().addEnd();
                end.exact = SettingsManager.getInputMethod() == EInputMethod.PLOTTING;
                ends = getEnds();
            }
            return ends.get(endIndex);
        }

        public void setArrowDiameter(Dimension arrowDiameter) {
            this.arrowDiameter = arrowDiameter;
        }
    }
}
