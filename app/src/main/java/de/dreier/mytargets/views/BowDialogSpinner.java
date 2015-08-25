/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class BowDialogSpinner extends DialogSpinner<Bow> {

    public BowDialogSpinner(Context context) {
        this(context, null);
    }

    public BowDialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        init();
    }

    private void init() {
        setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ItemSelectActivity.Bow.class);
            i.putExtra(ItemSelectActivity.ITEM, item);
            startIntent(i, data -> setItem((Bow) data.getSerializableExtra(ItemSelectActivity.ITEM)));
        });
        setAddButton((Button) mView.findViewById(R.id.add_bow),
                v -> getContext().startActivity(new Intent(getContext(), EditBowActivity.class)));
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView name = (TextView) mView.findViewById(R.id.name);
        name.setText(item.name);
        img.setImageDrawable(new RoundedAvatarDrawable(item.getThumbnail()));
    }

    public void setItemId(long bow) {
        setItem(DatabaseManager.getInstance(getContext()).getBow(bow));
    }
}
