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
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.gallery.HorizontalImageViewHolder;
import de.dreier.mytargets.base.gallery.OnImgClick;

public class HorizontalListAdapters extends RecyclerView.Adapter<HorizontalImageViewHolder> {
    private ArrayList<String> images;
    private Activity activity;
    private int selectedItem = -1;
    private OnImgClick imgClick;

    public HorizontalListAdapters(Activity activity, ArrayList<String> images, OnImgClick imgClick) {
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
        Picasso.with(activity).load(images.get(position)).into(holder.image);
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

        holder.image.setOnClickListener(view -> imgClick.onClick(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
}
