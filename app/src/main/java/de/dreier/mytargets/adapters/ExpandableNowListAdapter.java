/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.ItemBindingHolder;
import de.dreier.mytargets.utils.SelectableViewHolder;

public abstract class ExpandableNowListAdapter<HEADER extends IIdProvider, CHILD extends IIdProvider>
        extends RecyclerView.Adapter<ItemBindingHolder<IIdProvider>> {

    private static final int ITEM_TYPE = 2;
    private static final int HEADER_TYPE = 1;
    private final LongSparseArray<List<CHILD>> childMap = new LongSparseArray<>();
    private final ArrayList<Boolean> isOpen = new ArrayList<>();
    private final List<DataHolder> dataList = new ArrayList<>();
    private List<HEADER> listHeaders = new ArrayList<>();
    private PartitionDelegate<CHILD> partitionDelegate;

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

    @SuppressWarnings("unchecked")
    @Override
    public ItemBindingHolder<IIdProvider> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return (ItemBindingHolder<IIdProvider>) getTopLevelViewHolder(parent);
        } else {
            return (ItemBindingHolder<IIdProvider>) getSecondLevelViewHolder(parent);
        }
    }

    protected abstract HeaderBindingHolder<HEADER> getTopLevelViewHolder(ViewGroup parent);

    protected abstract SelectableViewHolder<CHILD> getSecondLevelViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(ItemBindingHolder<IIdProvider> viewHolder, int position) {
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

    private boolean isHeader(int position) {
        return position == -1 || dataList.get(position).getType() == ItemType.HEADER;
    }

    public void expandOrCollapse(int position) {
        int headerPosition = getHeaderCountUpToPosition(position);
        HEADER headerGroup = listHeaders.get(headerPosition);
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

    public IIdProvider getItem(int position) {
        if (position == -1) {
            return null;
        }
        return dataList.get(position).getData();
    }

    public void add(int pos, CHILD item) {
        dataList.add(pos, new DataHolder(item, ItemType.ITEM));
        long parent = partitionDelegate.getParentId(item);
        childMap.get(parent).add(pos - getItemCountUpToHeader(parent), item);
        notifyItemInserted(pos);
    }

    @SuppressWarnings("unchecked")
    public void remove(int pos) {
        DataHolder removed = dataList.remove(pos);
        CHILD data = (CHILD) removed.getData();
        long parent = partitionDelegate.getParentId(data);
        childMap.get(parent).remove(data);
        notifyItemRemoved(pos);
    }

    public void setList(List<HEADER> headers, List<CHILD> children, PartitionDelegate<CHILD> partitionDelegate) {
        this.partitionDelegate = partitionDelegate;
        List<Long> oldExpanded = getExpandedIds();
        listHeaders = headers;
        dataList.clear();
        childMap.clear();
        isOpen.clear();
        for (HEADER header : listHeaders) {
            childMap.put(header.getId(), new ArrayList<>());
        }
        for (CHILD child : children) {
            long parent = partitionDelegate.getParentId(child);
            childMap.get(parent).add(child);
        }
        for (HEADER header : listHeaders) {
            isOpen.add(false);
            dataList.add(new DataHolder(header, ItemType.HEADER));
        }
        setExpandedIds(oldExpanded);
        notifyDataSetChanged();
    }

    public void setList(List<HEADER> headers, List<CHILD> children, PartitionDelegate<CHILD> partitionDelegate, boolean opened) {
        this.partitionDelegate = partitionDelegate;
        listHeaders = headers;
        dataList.clear();
        childMap.clear();
        isOpen.clear();
        for (HEADER header : listHeaders) {
            childMap.put(header.getId(), new ArrayList<>());
        }
        for (CHILD child : children) {
            long parent = partitionDelegate.getParentId(child);
            childMap.get(parent).add(child);
        }
        for (HEADER header : listHeaders) {
            isOpen.add(opened);
            dataList.add(new DataHolder(header, ItemType.HEADER));
            if (opened) {
                for (CHILD item : childMap.get(header.getId())) {
                    dataList.add(new DataHolder(item, ItemType.ITEM));
                }
            }
        }
    }

    public List<Long> getExpandedIds() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < isOpen.size(); i++) {
            if (isOpen.get(i)) {
                ids.add(listHeaders.get(i).getId());
            }
        }
        return ids;
    }

    public void setExpandedIds(List<Long> expanded) {
        for (int i = 0; i < listHeaders.size(); i++) {
            long headerId = listHeaders.get(i).getId();
            boolean expand = expanded.contains(headerId);
            if (isOpen.get(i) != expand) {
                expandOrCollapse(getItemCountUpToHeader(headerId) - 1);
            }
        }
    }

    private enum ItemType {
        HEADER, ITEM
    }

    private class DataHolder {
        private final IIdProvider data;
        private final ItemType type;

        DataHolder(IIdProvider item, ItemType type) {
            this.data = item;
            this.type = type;
        }

        public long getId() {
            return data.getId();
        }

        public IIdProvider getData() {
            return data;
        }

        public ItemType getType() {
            return type;
        }
    }
}
