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

package de.dreier.mytargets.base.gallery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import org.parceler.Parcels;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.base.gallery.adapters.HorizontalListAdapters;
import de.dreier.mytargets.base.gallery.adapters.ViewPagerAdapter;
import de.dreier.mytargets.databinding.ActivityGalleryBinding;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;

public class GalleryActivity extends ChildActivityBase {
    public static final String EXTRA_END = "end";

    ViewPagerAdapter adapter;
    LinearLayoutManager mLayoutManager;
    HorizontalListAdapters hAdapter;

    private End end;

    public static IntentWrapper getIntent(End end) {
        return new IntentWrapper(GalleryActivity.class)
                .with(EXTRA_END, Parcels.wrap(end));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGalleryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        end = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_END));

        setSupportActionBar(binding.toolbar);

        ToolbarUtils.showHomeAsUp(this);
        ToolbarUtils.setTitle(this, getString(R.string.passe) + " " + (end.index + 1));

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        ArrayList<String> imageList = getDummyImageList();
        adapter = new ViewPagerAdapter(this, imageList, binding.toolbar, binding.imagesHorizontalList);
        binding.pager.setAdapter(adapter);

        hAdapter = new HorizontalListAdapters(this, imageList, pos -> binding.pager.setCurrentItem(pos, true));
        binding.imagesHorizontalList.setLayoutManager(mLayoutManager);
        binding.imagesHorizontalList.setAdapter(hAdapter);
        hAdapter.notifyDataSetChanged();

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                binding.imagesHorizontalList.smoothScrollToPosition(position);
                hAdapter.setSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int currentPos = 0;
        hAdapter.setSelectedItem(currentPos);
        binding.pager.setCurrentItem(currentPos);
    }

    private ArrayList<String> getDummyImageList() {
        ArrayList<String> imagesList = new ArrayList<>();
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05110349/20160731-igor-trepeshchenok-barnimages-08-768x509.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095154/tumblr_oawfisUmZo1u7ns0go1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095153/tumblr_obbkeo3lZW1ted1sho1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095153/tumblr_obaxpnJbKg1sfie3io1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095153/tumblr_obdehwWneK1slhhf0o1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095152/2016-08-01-roman-drits-barnimages-005-768x512.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095151/2016-08-01-roman-drits-barnimages-003-768x512.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095149/tumblr_obbjwp1bDz1ted1sho1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095151/tumblr_oawfhnxNjL1u7ns0go1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095150/tumblr_ob6xvqXLoB1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/05095148/20160731-igor-trepeshchenok-barnimages-10-768x512.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092421/tumblr_oawfgd2G941u7ns0go1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092423/tumblr_ob6xutS8N21tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092421/tumblr_o86sgm6F7a1ted1sho1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092420/tumblr_ob6xudqW4U1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092420/2016-08-01-roman-drits-barnimages-002-768x512.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092418/tumblr_o97fatuGnd1ted1sho1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092420/tumblr_oawff12j9L1u7ns0go1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092420/2016-08-01-roman-drits-barnimages-001-768x512.jpg");
        imagesList.add("http://1x402i15i5vh15yage3fafmg.wpengine.netdna-cdn.com/wp-content/uploads/2016/08/tumblr_oawfdsEx2w1u7ns0go1_500-1.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/08/03092417/tumblr_o97gyqSK3k1ted1sho1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092417/20160731-igor-trepeshchenok-barnimages-07-768x512.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092605/tumblr_ob6wjiCBUh1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092604/tumblr_ob6wkn58cJ1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092416/tumblr_ob6wk7mns81tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092413/tumblr_ob6vv04yIP1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092414/tumblr_ob6vk1bBPa1tlwzgvo1_500.jpg");
        imagesList.add("http://static0.passel.co/wp-content/uploads/2016/07/03092404/tumblr_o97ipvkger1ted1sho1_500.jpg");
        return imagesList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
