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
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.db.Image;
import de.dreier.mytargets.shared.utils.ImageList;
import de.dreier.mytargets.shared.utils.SharedUtils;
import de.dreier.mytargets.utils.Utils;

public class ViewPagerAdapter extends PagerAdapter {

    private Activity activity;
    @Nullable
    private LayoutInflater layoutInflater;
    private ImageList images;
    private boolean isShowing = true;
    private Toolbar toolbar;
    private RecyclerView imagesHorizontalList;

    public ViewPagerAdapter(@NonNull Activity activity, ImageList images, Toolbar toolbar, RecyclerView imagesHorizontalList) {
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
        this.toolbar = toolbar;
        this.imagesHorizontalList = imagesHorizontalList;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return SharedUtils.equals(view, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

        final PhotoView imageView = itemView.findViewById(R.id.iv);
        Image image = images.get(position);
        Picasso.with(activity)
                .load(new File(activity.getFilesDir(), image.getFileName()))
                .fit()
                .centerInside()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setOnPhotoTapListener((view, x, y) -> toggleToolbar());
                    }

                    @Override
                    public void onError() {

                    }
                });

        container.addView(itemView);
        return itemView;
    }

    private void toggleToolbar() {
        if (isShowing) {
            isShowing = false;
            toolbar.animate()
                    .translationY(-toolbar.getBottom())
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
            imagesHorizontalList.animate()
                    .translationY(imagesHorizontalList.getBottom())
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
            Utils.hideSystemUI(activity);
        } else {
            isShowing = true;
            toolbar.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            imagesHorizontalList.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            Utils.showSystemUI(activity);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
