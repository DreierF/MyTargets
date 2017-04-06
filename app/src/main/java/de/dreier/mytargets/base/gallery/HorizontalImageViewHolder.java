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

package de.dreier.mytargets.base.gallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import de.dreier.mytargets.R;

public class HorizontalImageViewHolder extends RecyclerView.ViewHolder {
    public ImageView camera;
    public ImageView image;

    public HorizontalImageViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.iv);
        camera = (ImageView) itemView.findViewById(R.id.camera);
    }
}
