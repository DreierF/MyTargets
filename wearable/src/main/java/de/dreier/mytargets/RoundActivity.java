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
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.databinding.ActivityRoundBinding;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.views.EndView;
import icepick.State;

import static de.dreier.mytargets.WearableListener.EXTRA_INFO;

/**
 * Demonstrates use of Navigation and Action Drawers on Android Wear.
 */
public class RoundActivity extends WearableActivity {

    public static final String EXTRA_ROUND = "round";
    public static final String EXTRA_TIMER = "timer";

    @State
    private Round round;

    @State
    boolean timerEnabled = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationInfo info = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_INFO));
            round = info.round;
            loadData();
        }
    };
    private ActivityRoundBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_round);

        setAmbientEnabled();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            round = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_ROUND));
            timerEnabled = intent.getExtras().getBoolean(EXTRA_TIMER);
        }

        loadData();

        // Peeks action drawer on the bottom.
        binding.drawerLayout.peekDrawer(Gravity.BOTTOM);

        binding.primaryActionTimer.setOnClickListener(view -> toggleTimer());
        binding.primaryActionTimer.setImageResource(
                timerEnabled ? R.drawable.ic_traffic_white_24dp
                        : R.drawable.ic_timer_off_white_24dp);

        final IntentFilter intentFilter = new IntentFilter(WearableListener.BROADCAST_TRAINING_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void loadData() {
        binding.recyclerViewEnds.setAdapter(new EndAdapter(round.getEnds()));
    }

    private void addEnd() {
        final Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(InputActivity.EXTRA_ROUND, Parcels.wrap(round));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        if(timerEnabled) {
            startActivity(new Intent(this, TimerActivity.class));
        }
    }

    public void toggleTimer() {
        timerEnabled = !timerEnabled;
        binding.primaryActionTimer.setImageResource(
                timerEnabled ? R.drawable.ic_traffic_white_24dp
                        : R.drawable.ic_timer_off_white_24dp);
    }

    private class EndAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<End> ends;

        public EndAdapter(List<End> ends) {
            this.ends = ends;
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
                ((ViewHolder) holder).end.setText(getString(R.string.end_n, end.index + 1));
                ((ViewHolder) holder).shots.setShots(round.getTarget(), end.getShots());
            }
        }

        @Override
        public int getItemCount() {
            return ends.size() + 1;
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
