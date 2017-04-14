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
package de.dreier.mytargets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.databinding.ActivityRoundBinding;
import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.views.EndView;
import de.dreier.mytargets.utils.WearSettingsManager;
import de.dreier.mytargets.utils.WearWearableClient;
import icepick.Icepick;
import icepick.State;

import static de.dreier.mytargets.shared.wearable.WearableClientBase.BROADCAST_TIMER_SETTINGS_FROM_REMOTE;
import static de.dreier.mytargets.utils.WearWearableClient.BROADCAST_TRAINING_UPDATED;

public class RoundActivity extends WearableActivity {

    public static final String EXTRA_ROUND = "round";

    private ActivityRoundBinding binding;

    @State(ParcelsBundler.class)
    Round round;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BROADCAST_TRAINING_UPDATED:
                    TrainingInfo info = Parcels
                            .unwrap(intent.getParcelableExtra(WearWearableClient.EXTRA_INFO));
                    round = info.round;
                    showRoundData();
                    break;
                case BROADCAST_TIMER_SETTINGS_FROM_REMOTE:
                    applyTimerState();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_round);

        setAmbientEnabled();

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                round = Parcels.unwrap(intent.getParcelableExtra(EXTRA_ROUND));
            }
        }

        showRoundData();

        binding.drawerLayout.peekDrawer(Gravity.BOTTOM);

        // Replaces the on click behaviour that open the (empty) drawer
        LinearLayout peekView = ((LinearLayout) binding.primaryActionTimer.getParent());
        ViewGroup peekContainer = ((ViewGroup) peekView.getParent());
        peekContainer.setOnClickListener(view -> toggleTimer());
        applyTimerState();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_TRAINING_UPDATED);
        filter.addAction(BROADCAST_TIMER_SETTINGS_FROM_REMOTE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        binding.drawerLayout.setBackgroundResource(R.color.md_black_1000);
        binding.recyclerViewEnds.getAdapter().notifyDataSetChanged();
        binding.wearableDrawerView.setVisibility(View.INVISIBLE);
        binding.time.setVisibility(View.VISIBLE);
        binding.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        binding.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        binding.drawerLayout.setBackgroundResource(R.color.md_wear_green_dark_background);
        binding.recyclerViewEnds.getAdapter().notifyDataSetChanged();
        binding.wearableDrawerView.setVisibility(View.VISIBLE);
        binding.time.setVisibility(View.GONE);
    }

    private void showRoundData() {
        boolean showAddEnd =
                round.maxEndCount == null || round.maxEndCount > round.getEnds().size();
        binding.recyclerViewEnds.setAdapter(new EndAdapter(round.getEnds(), showAddEnd));
        binding.recyclerViewEnds.scrollToPosition(round.getEnds().size());
    }

    private void addEnd() {
        final Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(InputActivity.EXTRA_ROUND, Parcels.wrap(round));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        TimerSettings timerSettings = WearSettingsManager.getTimerSettings();
        if (timerSettings.enabled) {
            Intent intentTimer = new Intent(this, TimerActivity.class);
            intentTimer.putExtra(TimerActivity.EXTRA_TIMER_SETTINGS, Parcels.wrap(timerSettings));
            startActivity(intentTimer);
        }
    }

    public void toggleTimer() {
        TimerSettings timerSettings = WearSettingsManager.getTimerSettings();
        timerSettings.enabled = !timerSettings.enabled;
        ApplicationInstance.wearableClient.sendTimerSettingsFromLocal(timerSettings);
        applyTimerState();
    }

    private void applyTimerState() {
        TimerSettings timerSettings = WearSettingsManager.getTimerSettings();
        binding.primaryActionTimer.setImageResource(
                timerSettings.enabled ? R.drawable.ic_traffic_white_24dp
                        : R.drawable.ic_timer_off_white_24dp);
    }

    private class EndAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<End> ends;
        private final boolean showAddEnd;

        public EndAdapter(List<End> ends, boolean showAddEnd) {
            this.ends = ends;
            this.showAddEnd = showAddEnd;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == 0) {
                View view = inflater.inflate(R.layout.item_end, parent, false);
                return new ViewHolder(view);
            } else {
                View view = inflater.inflate(R.layout.item_inline_button, parent, false);
                return new InlineButtonViewHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == ends.size() ? 1 : 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                End end = ends.get(position);
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.end.setText(getString(R.string.end_n, end.index + 1));
                viewHolder.shots.setShots(round.getTarget(), end.getShots());

                viewHolder.end.setTextColor(getResources().getColor(
                        isAmbient() ? R.color.md_white_1000 :
                                R.color.md_wear_green_active_ui_element));
                viewHolder.shots.setAmbientMode(isAmbient());
                viewHolder.itemView.setBackgroundColor(getResources().getColor(
                        isAmbient() ? R.color.md_black_1000 :
                                R.color.md_wear_green_lighter_background));
            } else if(holder instanceof InlineButtonViewHolder) {
                holder.itemView.setVisibility(isAmbient() ? View.INVISIBLE : View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return ends.size() + (showAddEnd ? 1 : 0);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView end;
        private final EndView shots;

        public ViewHolder(View itemView) {
            super(itemView);
            end = (TextView) itemView.findViewById(R.id.end);
            shots = (EndView) itemView.findViewById(R.id.shoots);
        }
    }

    private class InlineButtonViewHolder extends RecyclerView.ViewHolder {

        public InlineButtonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> addEnd());
        }
    }
}
