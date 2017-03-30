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
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.db.Image;
import de.dreier.mytargets.shared.utils.SharedUtils;
import de.dreier.mytargets.utils.Utils;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewPagerAdapter extends PagerAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<? extends Image> images;
    private boolean isShowing = true;
    private Toolbar toolbar;
    private RecyclerView imagesHorizontalList;

    public ViewPagerAdapter(Activity activity, List<? extends Image> images, Toolbar toolbar, RecyclerView imagesHorizontalList) {
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv);
        Image image = images.get(position);
        Picasso.with(activity)
                .load(new File(image.getFileName()))
                .fit()
                .centerInside()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        private PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
                        photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float x, float y) {
                                toggleToolbar();
                            }

                            @Override
                            public void onOutsidePhotoTap() {

                            }
                        });
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
