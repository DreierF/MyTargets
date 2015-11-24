/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.models.IdProvider;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.ItemBindingHolder;
import de.dreier.mytargets.utils.SelectableViewHolder;

public abstract class ExpandableNowListAdapter<HEADER extends IdProvider, CHILD extends IdProvider>
        extends RecyclerView.Adapter<ItemBindingHolder<IdProvider>> {

    public static final int ITEM_TYPE = 2;
    public static final int ITEM_TYPE_2 = 3;
    private static final int HEADER_TYPE = 1;
    private final HashMap<Long, List<CHILD>> childMap = new HashMap<>();
    private final ArrayList<Boolean> isOpen = new ArrayList<>();
    private final List<DataHolder> dataList = new ArrayList<>();
    private List<HEADER> mListHeaders = new ArrayList<>();

    @Override
    public long getItemId(int position) {
        if (position == -1) {
            return 0;
        }
        return dataList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getType() == ItemType.ITEM) {
            return ITEM_TYPE;
        }
        return HEADER_TYPE;
    }

    @Override
    public ItemBindingHolder<IdProvider> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return (ItemBindingHolder<IdProvider>) getTopLevelViewHolder(parent);
        } else {
            return (ItemBindingHolder<IdProvider>) getSecondLevelViewHolder(parent);
        }
    }

    protected abstract HeaderBindingHolder<HEADER> getTopLevelViewHolder(ViewGroup parent);

    protected abstract SelectableViewHolder<CHILD> getSecondLevelViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(ItemBindingHolder<IdProvider> viewHolder, int position) {
        if (position == -1) {
            return;
        }
        final DataHolder dh = dataList.get(position);
        if (viewHolder instanceof HeaderBindingHolder) {
            HeaderBindingHolder header = (HeaderBindingHolder) viewHolder;
            int headerPosition = getHeaderCountUpToPosition(position);
            header.setExpandOnClickListener(v -> expandOrCollapse(dataList.indexOf(dh)),
                    isOpen.get(headerPosition));
        }
        viewHolder.bindCursor(dh.getData());
    }

    private int getHeaderCountUpToPosition(int position) {
        int counter = 0;
        for (int i = 0; i < position; i++) {
            if (isHeader(i)) {
                counter++;
            }
        }
        return counter;
    }

    private int getItemCountUpToHeader(long header) {
        int items = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getType() == ItemType.HEADER && dataList.get(i).getId() == header) {
                items++;
                break;
            }
            items++;
        }
        return items;
    }

    public boolean isHeader(int position) {
        return position == -1 || dataList.get(position).getType() == ItemType.HEADER;
    }

    public void expandOrCollapse(int position) {
        int headerPosition = getHeaderCountUpToPosition(position);
        HEADER headerGroup = mListHeaders.get(headerPosition);
        List<CHILD> children = childMap.get(headerGroup.getId());
        int childLength = children.size();
        if (!isOpen.get(headerPosition)) {
            for (int i = 0; i < childLength; i++) {
                dataList.add(position + i + 1, new DataHolder(children.get(i), ItemType.ITEM));
            }
            notifyItemRangeInserted(position + 1, childLength);
        } else {
            for (int i = 0; i < childLength; i++) {
                dataList.remove(position + 1);
            }
            notifyItemRangeRemoved(position + 1, childLength);
        }
        isOpen.set(headerPosition, !isOpen.get(headerPosition));
    }

    public IdProvider getItem(int position) {
        if (position == -1) {
            return null;
        }
        return dataList.get(position).getData();
    }

    public void add(int pos, CHILD item) {
        dataList.add(pos, new DataHolder(item, ItemType.ITEM));
        long parent = item.getParentId();
        childMap.get(parent).add(pos - getItemCountUpToHeader(parent), item);
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        DataHolder removed = dataList.remove(pos);
        IdProvider data = removed.getData();
        long parent = data.getParentId();
        childMap.get(parent).remove(data);
        notifyItemRemoved(pos);
    }

    public void setList(List<HEADER> headers, List<CHILD> children) {
        HashSet<Long> oldExpanded = getExpandedIds();
        mListHeaders = headers;
        dataList.clear();
        childMap.clear();
        isOpen.clear();
        for (HEADER header : mListHeaders) {
            childMap.put(header.getId(), new ArrayList<>());
        }
        for (CHILD child : children) {
            long parent = child.getParentId();
            childMap.get(parent).add(child);
        }
        for (HEADER header : mListHeaders) {
            isOpen.add(false);
            dataList.add(new DataHolder(header, ItemType.HEADER));
        }
        setExpandedIds(oldExpanded);
    }

    public void setList(List<HEADER> headers, List<CHILD> children, boolean opened) {
        mListHeaders = headers;
        dataList.clear();
        childMap.clear();
        isOpen.clear();
        for (HEADER header : mListHeaders) {
            childMap.put(header.getId(), new ArrayList<>());
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

    public int getMaxSpan() {
        return 1;
    }

    public HashSet<Long> getExpandedIds() {
        HashSet<Long> ids = new HashSet<>();
        for (int i = 0; i < isOpen.size(); i++) {
            if (isOpen.get(i)) {
                ids.add(mListHeaders.get(i).getId());
            }
        }
        return ids;
    }

    public void setExpandedIds(HashSet<Long> expanded) {
        for (int i = 0; i < mListHeaders.size(); i++) {
            long headerId = mListHeaders.get(i).getId();
            boolean expand = expanded.contains(headerId);
            if (isOpen.get(i) != expand) {
                expandOrCollapse(getItemCountUpToHeader(headerId) - 1);
            }
        }
    }

    protected enum ItemType {
        HEADER, ITEM
    }

    private class DataHolder {
        private final IdProvider data;
        private final ItemType type;

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
