/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dreier.mytargets.models.IdProvider;


public abstract class ExpandableNowListAdapter<HEADER extends IdProvider, CHILD extends IdProvider>
        extends RecyclerView.Adapter<CardViewHolder<IdProvider>> {

    public static final int HEADER_TYPE = 3;
    public static final int ITEM_TYPE = 2;
    public static final int PLACEHOLDER_HEADER = 1;

    private ArrayList<HEADER> mListHeaders = new ArrayList<>();
    private HashMap<Long, List<CHILD>> childMap = new HashMap<>();
    private ArrayList<Boolean> isOpen = new ArrayList<>();
    protected int headerHeight = 0;
    private List<DataHolder> dataList = new ArrayList<>();

    @Override
    public long getItemId(int position) {
        if (position == 0 && headerHeight > 0) {
            return 0;
        } else if (headerHeight > 0) {
            return dataList.get(position - 1).getId();
        } else {
            return dataList.get(position).getId();
        }
    }

    @Override
    public int getItemCount() {
        int header = headerHeight > 0 ? 1 : 0;
        return dataList.size() + header;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerHeight > 0) {
            return PLACEHOLDER_HEADER;
        } else if (headerHeight > 0) {
            position--;
        }
        if (dataList.get(position).getType() == ItemType.ITEM) {
            return ITEM_TYPE;
        }
        return HEADER_TYPE;
    }

    @Override
    public CardViewHolder<IdProvider> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PLACEHOLDER_HEADER) {
            View paddingView = new View(parent.getContext());
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, headerHeight);
            paddingView.setLayoutParams(lp);
            paddingView.setClickable(true);
            return new StaticViewHolder(paddingView);
        } else if (viewType == ITEM_TYPE) {
            return (CardViewHolder<IdProvider>) getSecondLevelViewHolder(parent);
        } else {
            return (CardViewHolder<IdProvider>) getTopLevelViewHolder(parent);
        }
    }

    protected abstract CardViewHolder<HEADER> getTopLevelViewHolder(ViewGroup parent);

    protected abstract CardViewHolder<CHILD> getSecondLevelViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(CardViewHolder<IdProvider> viewHolder, int position) {
        if (position == 0 && headerHeight > 0) {
            return;
        }
        int index = position;
        if (headerHeight > 0) {
            index--;
        }
        final DataHolder dh = dataList.get(index);
        if (getItemViewType(position) == HEADER_TYPE) {
            viewHolder.setExpandOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandOrCollapse(dataList.indexOf(dh));
                }
            });
        }
        viewHolder.bindCursor(dh.getData());
    }

    private int getHeaderCountUpToPosition(int position) {
        int counter = 0;
        for (int i = 0; i < position; i++) {
            counter += dataList.get(i).getType() == ItemType.HEADER ? 1 : 0;
        }
        return counter;
    }

    public void expandOrCollapse(int position) {
        int headerPosition = getHeaderCountUpToPosition(position);
        HEADER headerGroup = mListHeaders.get(headerPosition);
        List<CHILD> children = childMap.get(headerGroup.getId());
        int childLength = children.size();
        int realPos = position + (headerHeight > 0 ? 1 : 0);
        if (!isOpen.get(headerPosition)) {
            for (int i = 0; i < childLength; i++) {
                dataList.add(position + i + 1, new DataHolder(children.get(i), ItemType.ITEM));
            }
            notifyItemRangeInserted(realPos + 1, childLength);
        } else {
            for (int i = 0; i < childLength; i++) {
                dataList.remove(position + 1);
            }
            notifyItemRangeRemoved(realPos + 1, childLength);
        }
        isOpen.set(headerPosition, !isOpen.get(headerPosition));
    }

    public IdProvider getItem(int pos) {
        if (pos == 0 && headerHeight > 0) {
            return null;
        }
        if (headerHeight > 0) {
            pos--;
        }
        return dataList.get(pos).getData();
    }


    public void remove(int pos) {
        if (headerHeight > 0) {
            pos--;
        }
        DataHolder removed = dataList.remove(pos);
        long parent = removed.getData().getParentId();
        childMap.get(parent).remove(removed.getData());
        notifyItemRemoved(pos);
    }

    public void setList(ArrayList<HEADER> headers, ArrayList<CHILD> children, boolean opened) {
        mListHeaders = headers;
        dataList.clear();
        childMap.clear();
        isOpen.clear();
        for (HEADER header : mListHeaders) {
            childMap.put(header.getId(), new ArrayList<CHILD>());
        }
        for (CHILD child : children) {
            long parent = child.getParentId();
            childMap.get(parent).add(child);
        }
        for (HEADER header : mListHeaders) {
            isOpen.add(opened);
            dataList.add(new DataHolder(header, ItemType.HEADER));
            if (opened) {
                for (CHILD item : childMap.get(header.getId())) {
                    dataList.add(new DataHolder(item, ItemType.ITEM));
                }
            }
        }

    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public class StaticViewHolder extends CardViewHolder<IdProvider> {
        public StaticViewHolder(View itemView) {
            super(itemView, null, null);
        }

        @Override
        public void bindCursor() {
        }
    }


    protected enum ItemType {
        HEADER, ITEM
    }

    private class DataHolder {
        private IdProvider data;
        private ItemType type;

        public DataHolder(IdProvider item, ItemType type) {
            this.data = item;
            this.type = type;
        }

        public long getId() {
            return data.getId();
        }

        public IdProvider getData() {
            return data;
        }

        public ItemType getType() {
            return type;
        }
    }
}
