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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableRecyclerView;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.views.EndView;

/**
 * Demonstrates use of Navigation and Action Drawers on Android Wear.
 */
public class RoundActivity extends WearableActivity {

    boolean timerEnabled = false;
    private ImageView timerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_round);
        setAmbientEnabled();

        // Main Wearable Drawer Layout that wraps all content
        WearableDrawerLayout wearableDrawerLayout = (WearableDrawerLayout) findViewById(
                R.id.drawer_layout);

        WearableRecyclerView recyclerView = (WearableRecyclerView) findViewById(
                R.id.recyclerViewEnds);
        recyclerView.setAdapter(new EndAdapter());

        // Peeks action drawer on the bottom.
        wearableDrawerLayout.peekDrawer(Gravity.BOTTOM);

        timerButton = (ImageView) findViewById(R.id.primaryActionTimer);
        timerButton.setOnClickListener(view -> toggleTimer());
        timerButton.setImageResource(
                timerEnabled ? R.drawable.ic_traffic_white_24dp
                        : R.drawable.ic_timer_off_white_24dp);
    }

    private void addEnd() {
        final Intent intent = new Intent(this, InputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        if(timerEnabled) {
            startActivity(new Intent(this, TimerActivity.class));
        }
    }

    public void toggleTimer() {
        timerEnabled = !timerEnabled;
        timerButton.setImageResource(
                timerEnabled ? R.drawable.ic_traffic_white_24dp
                        : R.drawable.ic_timer_off_white_24dp);
    }

    private class EndAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
            return position == 9 ? 1 : 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).end.setText("End " + (position + 1));
                List<Shot> list = new ArrayList<>();
                list.add(getShot(0, 0));
                list.add(getShot(1, 1));
                list.add(getShot(2, 2));
                ((ViewHolder) holder).shots.setShots(new Target(1, 0), list);
            }
        }

        @NonNull
        public Shot getShot(int index, int scoringRing) {
            final Shot shot = new Shot(index);
            shot.scoringRing = scoringRing;
            return shot;
        }

        @Override
        public int getItemCount() {
            return 10;
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