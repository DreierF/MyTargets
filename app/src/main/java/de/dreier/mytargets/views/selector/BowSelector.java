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

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class BowSelector extends SelectorBase<Bow> {

    public BowSelector(Context context) {
        this(context, null);
    }

    public BowSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        setOnClickActivity(ItemSelectActivity.BowActivity.class);
        setAddButtonIntent(SimpleFragmentActivity.EditBowActivity.class, (data) -> setItemId(0));
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
        BowDataSource bowDataSource = new BowDataSource(getContext());
        if (bow > 0) {
            item = bowDataSource.get(bow);
        }
        if (item == null) {
            ArrayList<Bow> all = bowDataSource.getAll();
            if (all.size() > 0) {
                item = all.get(0);
            }
        }
        setItem(item);
    }
}
