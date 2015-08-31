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
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class BowSelector extends SelectorBase<Bow> {

    public BowSelector(Context context) {
        this(context, null);
    }

    public BowSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        setOnClickActivity(ItemSelectActivity.Bow.class);
        setAddButtonIntent(SimpleFragmentActivity.EditBowActivity.class);
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView name = (TextView) mView.findViewById(R.id.name);
        name.setText(item.name);
        img.setImageDrawable(new RoundedAvatarDrawable(item.getThumbnail()));
    }

    public void setItemId(long bow) {
        Bow item = null;
        if (bow > 0) {
            item = DatabaseManager.getInstance(getContext()).getBow(bow);
        }
        if (item == null) {
            item = DatabaseManager.getInstance(getContext()).getBows().get(0);
        }
        setItem(item);
    }
}
