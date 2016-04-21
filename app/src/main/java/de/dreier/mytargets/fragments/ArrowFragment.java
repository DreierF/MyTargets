/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.RoundedAvatarDrawable;
import de.dreier.mytargets.utils.SelectableViewHolder;

public class ArrowFragment extends EditableFragment<Arrow> implements View.OnClickListener {

    private ArrowDataSource arrowDataSource;

    public ArrowFragment() {
        itemTypeSelRes = R.plurals.arrow_selected;
        itemTypeDelRes = R.plurals.arrow_deleted;
        newStringRes = R.string.new_arrow;
    }

    @Override
    public Loader<List<Arrow>> onCreateLoader(int id, Bundle args) {
        arrowDataSource = new ArrowDataSource(getContext());
        return new DataLoader<>(getContext(), arrowDataSource, arrowDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Arrow>> loader, List<Arrow> data) {
        setList(arrowDataSource, data, new ArrowAdapter());
    }

    @Override
    protected void onEdit(Arrow item) {
        Intent i = new Intent(getActivity(), SimpleFragmentActivity.EditArrowActivity.class);
        i.putExtra(EditArrowFragment.ARROW_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onClick(View v) {
        startActivity(SimpleFragmentActivity.EditArrowActivity.class);
    }

    private class ArrowAdapter extends NowListAdapter<Arrow> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Arrow> {
        @Bind(R.id.name)
        TextView mName;

        @Bind(R.id.image)
        ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, ArrowFragment.this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
            mImg.setImageDrawable(new RoundedAvatarDrawable(mItem.getThumbnail()));
        }
    }
}

