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
import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

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
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.utils.ImageList;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.shared.wearable.WearableClientBase.BROADCAST_TIMER_SETTINGS_FROM_REMOTE;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;
import static de.dreier.mytargets.utils.Utils.getCurrentLocale;

public class InputActivity extends ChildActivityBase
        implements TargetViewBase.OnEndFinishedListener, TargetView.OnEndUpdatedListener,
        LoaderManager.LoaderCallbacks<LoaderResult> {

    static final String TRAINING_ID = "training_id";
    static final String ROUND_ID = "round_id";
    static final String END_INDEX = "end_ind";
    private static final int GALLERY_REQUEST_CODE = 1;

    @State
    LoaderResult data;

    private ActivityInputBinding binding;
    private boolean transitionFinished = true;
    @NonNull
    private ETrainingScope summaryShowScope = ETrainingScope.END;
    private TargetView targetView;

    @NonNull
    private BroadcastReceiver updateReceiver = new MobileWearableClient.EndUpdateReceiver() {

        @Override
        protected void onUpdate(Long trainingId, Long roundId, @NonNull End end) {
            Bundle extras = getIntent().getExtras();
            extras.putLong(TRAINING_ID, trainingId);
            extras.putLong(ROUND_ID, roundId);
            extras.putInt(END_INDEX, end.getIndex());
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
    public static IntentWrapper createIntent(@NonNull Round round) {
        return getIntent(round, 0);
    }

    @NonNull
    public static IntentWrapper getIntent(@NonNull Round round, int endIndex) {
        return new IntentWrapper(InputActivity.class)
                .with(TRAINING_ID, round.getTrainingId())
                .with(ROUND_ID, round.getId())
                .with(END_INDEX, endIndex);
    }

    private static boolean shouldShowRound(@NonNull Round r, ETrainingScope shotShowScope, Long roundId) {
        return shotShowScope != ETrainingScope.END
                && (shotShowScope == ETrainingScope.TRAINING || r.getId().equals(roundId));
    }

    private static boolean shouldShowEnd(@NonNull End end, Long currentEndId) {
        return !SharedUtils.INSTANCE.equals(end.getId(), currentEndId) && end.getExact();
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

        StateSaver.restoreInstanceState(this, savedInstanceState);
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
        Utils.setShowWhenLocked(this, SettingsManager.INSTANCE.getInputKeepAboveLockscreen());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageList imageList = GalleryActivity.getResult(data);
            this.data.getCurrentEnd().setImages(imageList.toEndImageList());
            for (String image : imageList.getRemovedImages()) {
                new File(getFilesDir(), image).delete();
            }
            this.data.getCurrentEnd().toEnd().save();
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
        SummaryConfiguration config = SettingsManager.INSTANCE.getInputSummaryConfiguration();
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
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        final MenuItem timer = menu.findItem(R.id.action_timer);
        final MenuItem newRound = menu.findItem(R.id.action_new_round);
        final MenuItem takePicture = menu.findItem(R.id.action_photo);
        if (targetView == null || data.getEnds().size() == 0) {
            takePicture.setVisible(false);
            timer.setVisible(false);
            newRound.setVisible(false);
        } else {
            takePicture.setVisible(Utils.hasCameraHardware(this));
            timer.setIcon(SettingsManager.INSTANCE.getTimerEnabled()
                    ? R.drawable.ic_timer_off_white_24dp
                    : R.drawable.ic_timer_white_24dp);
            timer.setVisible(true);
            timer.setChecked(SettingsManager.INSTANCE.getTimerEnabled());
            newRound.setVisible(data.getTraining().getTraining().getStandardRoundId() == null);
            takePicture.setVisible(Utils.hasCameraHardware(this));
            takePicture.setIcon(data.getCurrentEnd().getImages().isEmpty() ?
                    R.drawable.ic_photo_camera_white_24dp : R.drawable.ic_image_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                GalleryActivity.getIntent(new ImageList(data.getCurrentEnd().getImages()), getString(R.string.end_n, data.getEndIndex() + 1))
                        .withContext(this)
                        .forResult(GALLERY_REQUEST_CODE)
                        .start();
                return true;
            case R.id.action_comment:
                new MaterialDialog.Builder(this)
                        .title(R.string.comment)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("", data.getCurrentEnd().getEnd().getComment(), (dialog, input) -> {
                            data.getCurrentEnd().getEnd().setComment(input.toString());
                            data.getCurrentEnd().toEnd().save();
                        })
                        .negativeText(android.R.string.cancel)
                        .show();
                return true;
            case R.id.action_timer:
                boolean timerEnabled = !SettingsManager.INSTANCE.getTimerEnabled();
                SettingsManager.INSTANCE.setTimerEnabled(timerEnabled);
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(SettingsManager.INSTANCE.getTimerSettings());
                openTimer();
                item.setChecked(timerEnabled);
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_settings:
                SettingsActivity.Companion.getIntent(ESettingsScreens.INPUT)
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_new_round:
                EditRoundFragment.Companion.createIntent(data.getTraining().getTraining())
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<LoaderResult> onCreateLoader(int id, @NonNull Bundle args) {
        long trainingId = args.getLong(TRAINING_ID);
        long roundId = args.getLong(ROUND_ID);
        int endIndex = args.getInt(END_INDEX);
        return new UITaskAsyncTaskLoader(this, trainingId, roundId, endIndex);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult> loader, @NonNull LoaderResult data) {
        this.data = data;
        onDataLoadFinished();
        showEnd(data.getEndIndex());
    }

    private void onDataLoadFinished() {
        setTitle(data.getTraining().getTraining().getTitle());
        if (!binding.targetViewStub.isInflated()) {
            binding.targetViewStub.getViewStub().inflate();
        }
        targetView = (TargetView) binding.targetViewStub.getBinding().getRoot();
        targetView.initWithTarget(data.getCurrentRound().getRound().getTarget());
        targetView.setArrow(data.getArrowDiameter(), data.getTraining().getTraining().getArrowNumbering(), data
                .getMaxArrowNumber());
        targetView.setOnTargetSetListener(InputActivity.this);
        targetView.setUpdateListener(InputActivity.this);
        targetView.reloadSettings();
        targetView.setAggregationStrategy(SettingsManager.INSTANCE.getAggregationStrategy());
        targetView.setInputMethod(SettingsManager.INSTANCE.getInputMethod());
        updateOldShoots();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {

    }

    private void showEnd(int endIndex) {
        // Create a new end
        data.setAdjustEndIndex(endIndex);
        if (endIndex >= data.getEnds().size()) {
            AugmentedEnd end = data.getCurrentRound().addEnd();
            end.getEnd().setExact(SettingsManager.INSTANCE.getInputMethod() == EInputMethod.PLOTTING);
            updateOldShoots();
        }

        // Open timer if end has not been saved yet
        openTimer();
        updateEnd();
        supportInvalidateOptionsMenu();
    }

    public void updateOldShoots() {
        final AugmentedEnd currentEnd = data.getCurrentEnd();
        final Long currentRoundId = data.getCurrentRound().getRound().getId();
        final Long currentEndId = currentEnd.getEnd().getId();
        final ETrainingScope shotShowScope = SettingsManager.INSTANCE.getShowMode();
        final LoaderResult data = this.data;
        final Stream<Shot> shotStream = Stream.of(data.getTraining().getRounds())
                .filter((r) -> shouldShowRound(r.getRound(), shotShowScope, currentRoundId))
                .flatMap(r -> Stream.of(r.getEnds()))
                .filter((end) -> shouldShowEnd(end.getEnd(), currentEndId))
                .flatMap(p -> Stream.of(p.getShots()));
        targetView.setTransparentShots(shotStream.toList());
    }

    private void openTimer() {
        if (data.getCurrentEnd().isEmpty() && SettingsManager.INSTANCE.getTimerEnabled()) {
            if (transitionFinished) {
                TimerFragment.getIntent(true)
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
                TimerFragment.getIntent(true)
                        .withContext(InputActivity.this)
                        .start();
                getWindow().getSharedElementEnterTransition().removeListener(this);
            }
        });
    }

    private void updateEnd() {
        targetView.replaceWithEnd(data.getCurrentEnd().toEnd());
        final int totalEnds = data.getCurrentRound().getRound().getMaxEndCount() == null
                ? data.getEnds().size()
                : data.getCurrentRound().getRound().getMaxEndCount();
        binding.endTitle.setText(getString(R.string.end_x_of_y, data.getEndIndex() + 1, totalEnds));
        binding.roundTitle.setText(getString(
                R.string.round_x_of_y,
                data.getCurrentRound().getRound().getIndex() + 1,
                data.getTraining().getRounds().size()));
        updateNavigationButtons();
        updateWearNotification();
    }

    private void updateWearNotification() {
        ApplicationInstance.wearableClient.sendUpdateTrainingFromLocalBroadcast(data.getTraining());
    }

    private void updateNavigationButtons() {
        updatePreviousButton();
        updateNextButton();
    }

    private void updatePreviousButton() {
        final boolean isFirstEnd = data.getEndIndex() == 0;
        final boolean isFirstRound = data.getRoundIndex() == 0;
        boolean showPreviousRound = isFirstEnd && !isFirstRound;
        final boolean isEnabled = !isFirstEnd || !isFirstRound;
        final int color;
        if (showPreviousRound) {
            final Round round = data.getTraining().getRounds().get(data.getRoundIndex() - 1).getRound();
            binding.prev.setOnClickListener(view -> openRound(round, round.loadEnds().size() - 1));
            binding.prev.setText(R.string.previous_round);
            color = getResources().getColor(R.color.colorPrimary);
        } else {
            binding.prev.setOnClickListener(view -> showEnd(data.getEndIndex() - 1));
            binding.prev.setText(R.string.prev);
            color = Color.BLACK;
        }
        binding.prev.setTextColor(Utils.argb(isEnabled ? 0xFF : 0x42, color));
        binding.prev.setEnabled(isEnabled);
    }

    private void updateNextButton() {
        final boolean dataLoaded = data != null;
        boolean isLastEnd = dataLoaded &&
                data.getCurrentRound().getRound().getMaxEndCount() != null &&
                data.getEndIndex() + 1 == data.getCurrentRound().getRound().getMaxEndCount();
        final boolean hasOneMoreRound = dataLoaded &&
                data.getRoundIndex() + 1 < data.getTraining().getRounds().size();
        boolean showNextRound = isLastEnd && hasOneMoreRound;
        final boolean isEnabled = dataLoaded && (!isLastEnd || hasOneMoreRound);
        final int color;
        if (showNextRound) {
            final Round round = data.getTraining().getRounds().get(data.getRoundIndex() + 1).getRound();
            binding.next.setOnClickListener(view -> openRound(round, 0));
            binding.next.setText(R.string.next_round);
            color = getResources().getColor(R.color.colorPrimary);
        } else {
            binding.next.setOnClickListener(view -> showEnd(data.getEndIndex() + 1));
            binding.next.setText(R.string.next);
            color = Color.BLACK;
        }
        binding.next.setTextColor(Utils.argb(isEnabled ? 0xFF : 0x42, color));
        binding.next.setEnabled(isEnabled);
    }

    private void openRound(@NonNull Round round, int endIndex) {
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
        data.getCurrentEnd().toEnd().save();

        // Set current end score
        Score reachedEndScore = data.getCurrentRound().getRound().getTarget()
                .getReachedScore(data.getCurrentEnd().toEnd());
        binding.endScore.setText(reachedEndScore.toString());

        // Set current round score
        Score reachedRoundScore = Stream.of(data.getEnds())
                .map(end -> data.getCurrentRound().getRound().getTarget().getReachedScore(end.toEnd()))
                .scoreSum();
        binding.roundScore.setText(reachedRoundScore.toString());

        // Set current training score
        Score reachedTrainingScore = Stream.of(data.getTraining().getRounds())
                .flatMap(r -> Stream.of(r.getEnds())
                        .map(end -> r.getRound().getTarget().getReachedScore(end.getEnd())))
                .scoreSum();
        binding.trainingScore.setText(reachedTrainingScore.toString());

        switch (summaryShowScope) {
            case END:
                binding.averageScore
                        .setText(reachedEndScore.getShotAverageFormatted(getCurrentLocale(this)));
                break;
            case ROUND:
                binding.averageScore
                        .setText(reachedRoundScore.getShotAverageFormatted(getCurrentLocale(this)));
                break;
            case TRAINING:
                binding.averageScore.setText(reachedTrainingScore
                        .getShotAverageFormatted(getCurrentLocale(this)));
                break;
            default:
                break;
        }
    }

    @Override
    public void onEndFinished(@NonNull List<Shot> shots) {
        data.getCurrentEnd().setShots(shots);
        data.getCurrentEnd().getEnd().setExact(targetView.getInputMode() == EInputMethod.PLOTTING);
        data.getCurrentEnd().toEnd().save();

        updateWearNotification();
        updateNavigationButtons();
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    private static class UITaskAsyncTaskLoader extends AsyncTaskLoader<LoaderResult> {
        private final long trainingId;
        private final long roundId;
        private final int endIndex;

        public UITaskAsyncTaskLoader(@NonNull Context context, long trainingId, long roundId, int endIndex) {
            super(context);
            this.trainingId = trainingId;
            this.roundId = roundId;
            this.endIndex = endIndex;
        }

        @Override
        public LoaderResult loadInBackground() {
            Training training = Training.Companion.get(trainingId);
            final LoaderResult result = new LoaderResult(new AugmentedTraining(training));
            result.setRoundId(roundId);
            result.setAdjustEndIndex(endIndex);

            if (training.getArrowId() != null) {
                Arrow arrow = training.getArrow();
                if (arrow != null) {
                    result.setArrow(arrow);
                }
            }
            final Bow bow = training.getBow();
            if (bow != null) {
                result.setSightMark(bow.loadSightSetting(result.getDistance()));
            }
            return result;
        }
    }

}
