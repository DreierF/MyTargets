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
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;

public class ArrowDialogSpinner extends DialogSpinner<Arrow> {

    public ArrowDialogSpinner(Context context) {
        this(context, null);
    }

    public ArrowDialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
        init();
    }

    private void init() {
        setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ItemSelectActivity.Arrow.class);
            i.putExtra(ItemSelectActivity.ITEM, item);
            startIntent(i, data -> setItem((Arrow) data.getSerializableExtra(ItemSelectActivity.ITEM)));
        });
        setAddButton((Button) mView.findViewById(R.id.add_arrow),
                v -> getContext().startActivity(new Intent(getContext(), EditArrowActivity.class)));
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView name = (TextView) mView.findViewById(R.id.name);
        name.setText(item.name);
        img.setImageDrawable(new RoundedAvatarDrawable(item.getThumbnail()));
    }

    public void setItemId(long arrow) {
        setItem(DatabaseManager.getInstance(getContext()).getArrow(arrow));
    }
}
