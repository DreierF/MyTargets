/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public abstract class ExpandableListAdapter<HEADER extends IIdProvider, CHILD extends IIdProvider>
        extends RecyclerView.Adapter<ItemBindingHolder<IIdProvider>> {

    private static final int ITEM_TYPE = 2;
    private static final int HEADER_TYPE = 1;
    private List<HeaderHolder> headersList = new ArrayList<>();
    private PartitionDelegate<HEADER, CHILD> partitionDelegate;
    private Comparator<HEADER> headerComparator;
    private Comparator<CHILD> childComparator;

    public ExpandableListAdapter(PartitionDelegate<HEADER, CHILD> partitionDelegate, Comparator<HEADER> headerComparator, Comparator<CHILD> childComparator) {
        this.partitionDelegate = partitionDelegate;
        this.headerComparator = headerComparator;
        this.childComparator = childComparator;
        setHasStableIds(true);
    }

    public IIdProvider getItem(int position) {
        HeaderHolder header = getHeaderForPosition(position);
        int pos = getHeaderRelativePosition(position);
        if (pos == 0) {
            return header.item;
        } else {
            return header.children.get(pos - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (HeaderHolder header : headersList) {
            count += header.getTotalSize();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return getHeaderRelativePosition(position) == 0 ? HEADER_TYPE : ITEM_TYPE;
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
        if (viewHolder instanceof HeaderBindingHolder) {
            HeaderHolder header = getHeaderForPosition(position);
            ((HeaderBindingHolder) viewHolder)
                    .setExpandOnClickListener(v -> expandOrCollapse(header), header.expanded);
        }
        viewHolder.bindCursor(getItem(position));
    }

    private HeaderHolder getHeaderForPosition(int position) {
        int items = 0;
        for (HeaderHolder header : headersList) {
            if (items == position) {
                return header;
            }
            if (items + header.children.size() >= position && header.expanded) {
                return header;
            }
            items += header.getTotalSize();
        }
        throw new IllegalStateException("Position is not in list!");
    }

    private int getHeaderRelativePosition(int position) {
        int items = 0;
        for (HeaderHolder header : headersList) {
            if (items == position) {
                return 0;
            }
            if (items + header.children.size() >= position && header.expanded) {
                return position - items;
            }
            items += header.getTotalSize();
        }
        throw new IllegalStateException("Position is not in list!");
    }

    public void expandOrCollapse(HeaderHolder header) {
        int childLength = header.children.size();
        if (!header.expanded) {
            notifyItemRangeInserted(header.getAbsolutePosition() + 1, childLength);
        } else {
            notifyItemRangeRemoved(header.getAbsolutePosition() + 1, childLength);
        }
        header.expanded = !header.expanded;
    }

    public void add(CHILD item) {
        addChildToMap(item);
        notifyDataSetChanged();
    }

    public void remove(CHILD item) {
        HEADER parent = partitionDelegate.getParent(item);
        int headerIndex = new HeaderHolder(parent).getHeaderIndex();
        if (headerIndex < 0) {
            return;
        }
        HeaderHolder header = headersList.get(headerIndex);
        header.remove(item);
        if (header.children.size() < 1) {
            headersList.remove(headerIndex);
        }
    }

    public void setList(List<CHILD> children) {
        List<Long> oldExpanded = getExpandedIds();
        fillChildMap(children);
        setExpandedIds(oldExpanded);
        notifyDataSetChanged();
    }

    public void setList(List<CHILD> children, boolean opened) {
        fillChildMap(children);
        expandAll(opened);
        notifyDataSetChanged();
    }

    protected void fillChildMap(List<CHILD> children) {
        headersList.clear();
        for (CHILD child : children) {
            addChildToMap(child);
        }
    }

    private void addChildToMap(CHILD child) {
        HEADER parent = partitionDelegate.getParent(child);
        HeaderHolder parentHolder = new HeaderHolder(parent);
        int pos = parentHolder.getHeaderIndex();
        if (pos < 0) {
            parentHolder.add(child);
            headersList.add(-pos - 1, parentHolder);
        } else {
            headersList.get(pos).add(child);
        }
    }

    public List<Long> getExpandedIds() {
        return Stream.of(headersList)
                .filter(h -> h.expanded)
                .map(h -> h.item.getId())
                .collect(Collectors.toList());
    }

    public void setExpandedIds(List<Long> expanded) {
        for (int i = 0; i < headersList.size(); i++) {
            final HeaderHolder header = headersList.get(i);
            header.expanded = expanded.contains(header.item.getId());
        }
    }

    private void expandAll(boolean expanded) {
        for (HeaderHolder header : headersList) {
            header.expanded = expanded;
        }
    }

    public CHILD getItemById(long id) {
        for (HeaderHolder header : headersList) {
            for (CHILD child : header.children) {
                if (child.getId() == id) {
                    return child;
                }
            }
        }
        return null;
    }

    public void expandFirst() {
        expandOrCollapse(headersList.get(0));
    }

    private class HeaderHolder implements Comparable<HeaderHolder> {
        HEADER item;
        boolean expanded;
        List<CHILD> children;

        public HeaderHolder(HEADER parent) {
            item = parent;
            expanded = false;
            children = new ArrayList<>();
        }

        public void add(CHILD item) {
            int pos = Collections.binarySearch(children, item, childComparator);
            if (pos < 0) {
                children.add(-pos - 1, item);
            } else {
                throw new IllegalArgumentException("Item must not be inserted twice!");
            }
        }

        @Override
        public int compareTo(@NonNull HeaderHolder headerHolder) {
            return headerComparator.compare(item, headerHolder.item);
        }

        public void remove(CHILD item) {
            children.remove(item);
        }

        public int getHeaderIndex() {
            return Collections.binarySearch(headersList, this);
        }

        public int getAbsolutePosition() {
            int headerIndex = getHeaderIndex();
            int pos = 0;
            for (int i = 0; i < headerIndex; i++) {
                pos += headersList.get(i).getTotalSize();
            }
            return pos;
        }

        public int getTotalSize() {
            return expanded ? 1 + children.size() : 1;
        }
    }
}
