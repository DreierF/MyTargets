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

package de.dreier.mytargets.features.bows;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.base.fragments.SelectPureListItemFragmentBase;
import de.dreier.mytargets.shared.models.db.Bow;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class BowListFragment extends SelectPureListItemFragmentBase<Bow> {

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<Bow> bows = Bow.getAll();
        return () -> {
            adapter.setList(bows);
            Bow bow = Parcels.unwrap(getArguments().getParcelable(ITEM));
            selectItem(binding.recyclerView, bow);
        };
    }
}
