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

package de.dreier.mytargets.base.gallery.adapters;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.gallery.HorizontalImageViewHolder;
import de.dreier.mytargets.base.gallery.OnImgClick;
import de.dreier.mytargets.shared.models.db.Image;

public class HorizontalListAdapters extends RecyclerView.Adapter<HorizontalImageViewHolder> {
    private List<? extends Image> images;
    private Activity activity;
    private int selectedItem = -1;
    private OnImgClick imgClick;

    public HorizontalListAdapters(Activity activity, List<? extends Image> images, OnImgClick imgClick) {
        this.activity = activity;
        this.images = images;
        this.imgClick = imgClick;
    }

    @Override
    public HorizontalImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_horizontal, parent, false));
    }

    @Override
    public void onBindViewHolder(HorizontalImageViewHolder holder, final int position) {
        if (position == images.size()) {
            holder.image.setVisibility(View.GONE);
            holder.camera.setVisibility(View.VISIBLE);
        } else {
            holder.camera.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Image image = images.get(position);
            Picasso.with(activity)
                    .load(new File(image.getFileName()))
                    .fit()
                    .into(holder.image);
            ColorMatrix matrix = new ColorMatrix();
            if (selectedItem != position) {
                matrix.setSaturation(0);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.image.setColorFilter(filter);
                holder.image.setAlpha(0.5f);
            } else {
                matrix.setSaturation(1);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.image.setColorFilter(filter);
                holder.image.setAlpha(1f);
            }
        }

        holder.itemView.setOnClickListener(view -> imgClick.onClick(position));
    }

    @Override
    public int getItemCount() {
        return images.size() + 1;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
}
