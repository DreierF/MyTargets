package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.util.ArrayList;

import de.dreier.mytargets.models.IdProvider;

/**
 * Created by Florian on 13.03.2015.
 */
public abstract class NowListAdapter<T extends IdProvider> extends RecyclerView.Adapter<CardViewHolder<T>> {

    protected ArrayList<T> mList = new ArrayList<>();
    private int headerHeight = 0;

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        int header = headerHeight > 0 ? 1 : 0;
        return mList.size() + header;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerHeight > 0) {
            return 0;
        } else if (headerHeight > 0) {
            return super.getItemViewType(position - 1) + 1;
        } else {
            return super.getItemViewType(position) + 1;
        }
    }

    @Override
    public final CardViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0 && headerHeight > 0) {
            View paddingView = new View(parent.getContext());
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, headerHeight);
            paddingView.setLayoutParams(lp);
            paddingView.setClickable(true);
            return new StaticViewHolder(paddingView);
        } else {
            return onCreateViewHolder(parent);
        }
    }

    protected abstract CardViewHolder<T> onCreateViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(CardViewHolder<T> viewHolder, int position) {
        if (position > 0 && headerHeight > 0) {
            viewHolder.bindCursor(mList.get(position - 1));
        } else if (headerHeight == 0) {
            viewHolder.bindCursor(mList.get(position));
        }
    }

    public T getItem(int pos) {
        return mList.get(pos);
    }

    public void remove(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setList(ArrayList<T> list) {
        mList = list;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public class StaticViewHolder extends CardViewHolder<T> {
        public StaticViewHolder(View itemView) {
            super(itemView, null, null);
        }

        @Override
        public void bindCursor() {
        }
    }
}
