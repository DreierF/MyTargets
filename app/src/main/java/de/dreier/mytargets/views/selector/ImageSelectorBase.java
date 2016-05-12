/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IDetailProvider;
import de.dreier.mytargets.shared.models.IImageProvider;


public abstract class ImageSelectorBase<T extends IImageProvider> extends SelectorBase<T> {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.details)
    TextView details;

    public ImageSelectorBase(Context context) {
        this(context, null);
    }

    public ImageSelectorBase(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_image_details);
    }

    @Override
    protected void bindView() {
        name.setText(item.getName(getContext()));
        if (item instanceof IDetailProvider) {
            details.setVisibility(VISIBLE);
            details.setText(((IDetailProvider) item).getDetails(getContext()));
        }
        image.setImageDrawable(item.getDrawable(getContext()));
        if(!isImageSelectable()) {
            image.setBackgroundDrawable(null);
        }
        invalidate();
    }

    protected boolean isImageSelectable() {
        return false;
    }

    void setTitle(@StringRes int title) {
        this.title.setVisibility(VISIBLE);
        this.title.setText(title);
    }
}
