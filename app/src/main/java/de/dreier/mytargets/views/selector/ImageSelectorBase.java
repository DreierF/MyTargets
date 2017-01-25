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

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringRes;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.SelectorItemImageDetailsBinding;
import de.dreier.mytargets.shared.models.IDetailProvider;
import de.dreier.mytargets.shared.models.IImageProvider;

public abstract class ImageSelectorBase<T extends IImageProvider> extends SelectorBase<T> {

    protected SelectorItemImageDetailsBinding binding;

    public ImageSelectorBase(Context context) {
        this(context, null);
    }

    public ImageSelectorBase(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_image_details);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding = DataBindingUtil.bind(view);
    }

    @Override
    protected void bindView() {
        binding.name.setText(item.getName());
        if (item instanceof IDetailProvider) {
            binding.details.setVisibility(VISIBLE);
            binding.details.setText(((IDetailProvider) item).getDetails(getContext()));
        }
        binding.image.setImageDrawable(item.getDrawable(getContext()));
    }

    protected void setTitle(@StringRes int title) {
        binding.title.setVisibility(VISIBLE);
        binding.title.setText(title);
    }
}
