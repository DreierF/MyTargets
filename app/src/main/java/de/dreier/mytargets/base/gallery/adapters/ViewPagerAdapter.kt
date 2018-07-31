/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.base.gallery.adapters

import android.app.Activity
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.dreier.mytargets.R
import de.dreier.mytargets.utils.ImageList
import de.dreier.mytargets.utils.Utils
import java.io.File

class ViewPagerAdapter(
    private val activity: Activity,
    private val images: ImageList,
    private val toolbar: Toolbar,
    private val imagesHorizontalList: RecyclerView
) : PagerAdapter() {
    private val layoutInflater = LayoutInflater.from(activity)
    private var isShowing = true

    override fun getCount(): Int {
        return images.size()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.pager_item, container, false)

        val imageView = itemView.findViewById<PhotoView>(R.id.iv)
        val image = images[position]
        Picasso.with(activity)
            .load(File(activity.filesDir, image.fileName))
            .fit()
            .centerInside()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView.setOnPhotoTapListener { _, _, _ -> toggleToolbar() }
                }

                override fun onError() {

                }
            })

        container.addView(itemView)
        return itemView
    }

    private fun toggleToolbar() {
        if (isShowing) {
            isShowing = false
            toolbar.animate()
                .translationY((-toolbar.bottom).toFloat())
                .setInterpolator(AccelerateInterpolator())
                .start()
            imagesHorizontalList.animate()
                .translationY(imagesHorizontalList.bottom.toFloat())
                .setInterpolator(AccelerateInterpolator())
                .start()
            Utils.hideSystemUI(activity)
        } else {
            isShowing = true
            toolbar.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .start()
            imagesHorizontalList.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .start()
            Utils.showSystemUI(activity)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}
