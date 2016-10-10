/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
        binding = DataBindingUtil.bind(mView);
    }

    @Override
    protected void bindView() {
        binding.name.setText(item.getName());
        if (item instanceof IDetailProvider) {
            binding.details.setVisibility(VISIBLE);
            binding.details.setText(((IDetailProvider) item).getDetails(getContext()));
        }
        binding.image.setImageDrawable(item.getDrawable(getContext()));
        invalidate();
    }

    void setTitle(@StringRes int title) {
        binding.title.setVisibility(VISIBLE);
        binding.title.setText(title);
    }
}
