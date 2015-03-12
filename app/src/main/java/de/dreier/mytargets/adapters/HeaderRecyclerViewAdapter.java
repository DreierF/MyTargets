package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class HeaderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MAX_COUNT = 1000;
    private static final int HEADER_VIEW_TYPE_OFFSET = 0;
    private static final int FOOTER_VIEW_TYPE_OFFSET = HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;
    private static final int CONTENT_VIEW_TYPE_OFFSET = FOOTER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;

    private int headerItemCount;
    private int contentItemCount;

    /**
     * {@inheritDoc}
     */
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Delegate to proper methods based on the viewType ranges.
        if (viewType >= HEADER_VIEW_TYPE_OFFSET && viewType < HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
            return onCreateHeaderItemViewHolder(parent, viewType - HEADER_VIEW_TYPE_OFFSET);
        } else if (viewType >= CONTENT_VIEW_TYPE_OFFSET && viewType < CONTENT_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
            return onCreateContentItemViewHolder(parent, viewType - CONTENT_VIEW_TYPE_OFFSET);
        } else {
            // This shouldn't happen as we check that the viewType provided by the client is valid.
            throw new IllegalStateException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Delegate to proper methods based on the viewType ranges.
        if (headerItemCount > 0 && position < headerItemCount) {
            onBindHeaderItemViewHolder(viewHolder, position);
        } else if (contentItemCount > 0 && position - headerItemCount < contentItemCount) {
            onBindContentItemViewHolder(viewHolder, position - headerItemCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getItemCount() {
        // Cache the counts and return the sum of them.
        headerItemCount = getHeaderItemCount();
        contentItemCount = getContentItemCount();
        return headerItemCount + contentItemCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getItemViewType(int position) {
        // Delegate to proper methods based on the position, but validate first.
        if (headerItemCount > 0 && position < headerItemCount) {
            return validateViewType(getHeaderItemViewType(position)) + HEADER_VIEW_TYPE_OFFSET;
        } else if (contentItemCount > 0 && position - headerItemCount < contentItemCount) {
            return validateViewType(getContentItemViewType(position - headerItemCount)) + CONTENT_VIEW_TYPE_OFFSET;
        } else {
            return validateViewType(0) + FOOTER_VIEW_TYPE_OFFSET;
        }
    }

    /**
     * Validates that the view type is within the valid range.
     *
     * @param viewType the view type.
     * @return the given view type.
     */
    private int validateViewType(int viewType) {
        if (viewType < 0 || viewType >= VIEW_TYPE_MAX_COUNT) {
            throw new IllegalStateException("viewType must be between 0 and " + VIEW_TYPE_MAX_COUNT);
        }
        return viewType;
    }

    /**
     * Notifies that a content item is inserted.
     *
     * @param position the position of the content item.
     */
    public final void notifyContentItemInserted(int position) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        if (position < 0 || position >= newContentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - " + (newContentItemCount - 1) + "].");
        }
        notifyItemInserted(position + newHeaderItemCount);
    }

    /**
     * Notifies that multiple content items are inserted.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeInserted(int positionStart, int itemCount) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newContentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - " + (newContentItemCount - 1) + "].");
        }
        notifyItemRangeInserted(positionStart + newHeaderItemCount, itemCount);
    }

    /**
     * Notifies that a content item is changed.
     *
     * @param position the position.
     */
    public final void notifyContentItemChanged(int position) {
        if (position < 0 || position >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemChanged(position + headerItemCount);
    }

    /**
     * Notifies that multiple content items are changed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeChanged(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > contentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemRangeChanged(positionStart + headerItemCount, itemCount);
    }

    /**
     * Notifies that an existing content item is moved to another position.
     *
     * @param fromPosition the original position.
     * @param toPosition   the new position.
     */
    public final void notifyContentItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= contentItemCount || toPosition >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition " + toPosition + " is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemMoved(fromPosition + headerItemCount, toPosition + headerItemCount);
    }

    /**
     * Notifies that a content item is removed.
     *
     * @param position the position.
     */
    public final void notifyContentItemRemoved(int position) {
        if (position < 0 || position >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemRemoved(position + headerItemCount);
    }

    /**
     * Notifies that multiple content items are removed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeRemoved(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > contentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemRangeRemoved(positionStart + headerItemCount, itemCount);
    }

    /**
     * Gets the header item view type. By default, this method returns 0.
     *
     * @param position the position.
     * @return the header item view type (within the range [0 - VIEW_TYPE_MAX_COUNT-1]).
     */
    protected int getHeaderItemViewType(int position) {
        return 0;
    }

    /**
     * Gets the content item view type. By default, this method returns 0.
     *
     * @param position the position.
     * @return the content item view type (within the range [0 - VIEW_TYPE_MAX_COUNT-1]).
     */
    protected int getContentItemViewType(int position) {
        return 0;
    }

    /**
     * Gets the header item count. This method can be called several times, so it should not calculate the count every time.
     *
     * @return the header item count.
     */
    protected abstract int getHeaderItemCount();

    /**
     * Gets the content item count. This method can be called several times, so it should not calculate the count every time.
     *
     * @return the content item count.
     */
    protected abstract int getContentItemCount();

    /**
     * This method works exactly the same as {@link #onCreateViewHolder(android.view.ViewGroup, int)}, but for header items.
     *
     * @param parent         the parent view.
     * @param headerViewType the view type for the header.
     * @return the view holder.
     */
    protected abstract RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType);

    /**
     * This method works exactly the same as {@link #onCreateViewHolder(android.view.ViewGroup, int)}, but for content items.
     *
     * @param parent          the parent view.
     * @param contentViewType the view type for the content.
     * @return the view holder.
     */
    protected abstract RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType);

    /**
     * This method works exactly the same as {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)}, but for header items.
     *
     * @param headerViewHolder the view holder for the header item.
     * @param position         the position.
     */
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {}

    /**
     * This method works exactly the same as {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)}, but for content items.
     *
     * @param contentViewHolder the view holder for the content item.
     * @param position          the position.
     */
    protected abstract void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position);

}