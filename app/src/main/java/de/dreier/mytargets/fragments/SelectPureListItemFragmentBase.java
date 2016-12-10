package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageSimpleBinding;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 *
 * */
public abstract class SelectPureListItemFragmentBase<T extends IIdProvider & IImageProvider & Comparable<T>> extends SelectItemFragmentBase<T> {

    protected FragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        mAdapter = new ListAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setVisibility(View.GONE);
        ToolbarUtils.showUpAsX(this);
        return binding.getRoot();
    }

    @Override
    public void onLongClick(SelectableViewHolder<T> holder) {
        onClick(holder, holder.getItem());
    }

    private class ListAdapter extends ListAdapterBase<T> {
        ListAdapter(Context context) {
            super(context);
        }

        @Override
        public SelectableViewHolder<T> onCreateViewHolder(ViewGroup parent) {
            return SelectPureListItemFragmentBase.this.onCreateViewHolder(inflater, parent);
        }
    }

    @NonNull
    protected SelectableViewHolder<T> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_image_simple, parent, false));
    }

    private class ViewHolder extends SelectableViewHolder<T> {
        ItemImageSimpleBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, SelectPureListItemFragmentBase.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.getName());
            binding.image.setImageDrawable(item.getDrawable(getContext()));
        }
    }
}
