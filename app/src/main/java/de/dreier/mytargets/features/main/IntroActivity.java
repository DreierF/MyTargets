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

package de.dreier.mytargets.features.main;

import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import de.dreier.mytargets.R;

public class IntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBackButton();

        enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.introBackground)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.intro_screen_1)
                .title(getString(R.string.intro_title_track_training_progress))
                .description(getString(R.string.intro_description_track_training_progress))
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.introBackground)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.intro_screen_2)
                .title(getString(R.string.intro_title_everything_in_one_place))
                .description(getString(R.string.intro_description_everything_in_one_place))
                .build());
    }
}