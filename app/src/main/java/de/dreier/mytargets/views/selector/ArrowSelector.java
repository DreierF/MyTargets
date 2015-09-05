/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ArrowActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class ArrowSelector extends SelectorBase<Arrow> {

    public ArrowSelector(Context context) {
        this(context, null);
    }

    public ArrowSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        init();
    }

    private void init() {
        setOnClickActivity(ArrowActivity.class);
        setAddButtonIntent(SimpleFragmentActivity.EditArrowActivity.class);
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView name = (TextView) mView.findViewById(R.id.name);
        name.setText(item.name);
        img.setImageDrawable(new RoundedAvatarDrawable(item.getThumbnail()));
    }

    public void setItemId(long arrow) {
        Arrow item = null;
        ArrowDataSource arrowDataSource = new ArrowDataSource(getContext());
        if (arrow > 0) {
            item = arrowDataSource.get(arrow);
        }
        if (item == null) {
            item = arrowDataSource.getAll().get(0);
        }
        setItem(item);
    }
}