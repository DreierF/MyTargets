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

package de.dreier.mytargets.features.help;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentWebBinding;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;

/**
 * Shows all rounds of one training.
 */
public class HelpFragment extends Fragment {

    protected FragmentWebBinding binding;

    @NonNull
    public static IntentWrapper getIntent() {
        return new IntentWrapper(HelpActivity.class);
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false);
        String prompt = getHelpHtmlPage();
        binding.webView
                .loadDataWithBaseURL("file:///android_asset/", prompt, "text/html", "utf-8", "");
        binding.webView.setHorizontalScrollBarEnabled(false);

        return binding.getRoot();
    }

    @NonNull
    private String getHelpHtmlPage() {
        String prompt = "";
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.help);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            prompt = new String(buffer);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prompt;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.statistics_scoresheet, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
