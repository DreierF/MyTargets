package de.dreier.mytargets.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ChipsViewBinding;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

public class ChipGroup extends ViewGroup {
    List<Tag> tagList = new ArrayList<>();

    private int horizontalSpacing = (int) dp2px(8.0f);

    private int verticalSpacing = (int) dp2px(4.0f);

    /**
     * Listener used to dispatch tag click event.
     */
    private OnSelectionChangedListener mOnSelectionChangedListener;

    public ChipGroup(Context context) {
        this(context, null);
    }

    public ChipGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else { // If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
            }
        }
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(List<Tag> tags) {
        removeAllViews();
        tagList.clear();
        tagList.addAll(tags);
        for (final Tag tag : tagList) {
            appendTag(tag);
        }
    }

    /**
     * Returns the tag list in group.
     *
     * @return the tag list.
     */
    public List<Tag> getCheckedTags() {
        return Stream.of(tagList)
                .filter(value -> value.isChecked)
                .collect(Collectors.toList());
    }

    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    protected void appendTag(Tag tag) {
        ChipsViewBinding binding = tag.getView(getContext(), this);
        binding.getRoot().setOnClickListener(v -> {
            tag.isChecked = !tag.isChecked;
            binding.getRoot().setActivated(!tag.isChecked);
            if (mOnSelectionChangedListener != null) {
                mOnSelectionChangedListener.onSelectionChanged(tag);
            }
        });
        addView(binding.getRoot());
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ChipGroup.LayoutParams(getContext(), attrs);
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnSelectionChangedListener l) {
        mOnSelectionChangedListener = l;
    }

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnSelectionChangedListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag that was clicked.
         */
        void onSelectionChanged(Tag tag);
    }

    /**
     * Per-child layout information for layouts.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

    @Parcel
    public static class Tag {

        private static final int CHIP_HEIGHT = 32; // dp

        public long id;
        public String text;
        public byte[] image;
        public boolean isChecked = false;
        private transient Bitmap thumbnail;

        public Tag(long id, String text, boolean isChecked) {
            this(id, text, null, isChecked);
        }

        @ParcelConstructor
        public Tag(long id, String text, byte[] image, boolean isChecked) {
            this.id = id;
            this.text = text;
            this.isChecked = isChecked;
            this.image = image;
        }

        public ChipsViewBinding getView(Context context, ViewGroup parent) {
            ChipsViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chips_view, parent, false);
            binding.setTag(this);
            binding.getRoot().setActivated(!isChecked);
            float mDensity = context.getResources().getDisplayMetrics().density;
            binding.getRoot().setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (CHIP_HEIGHT * mDensity)));
            return binding;
        }

        public Drawable getDrawable() {
            if (image == null) {
                return null;
            }
            if (thumbnail == null) {
                thumbnail = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            return new RoundedAvatarDrawable(thumbnail);
        }
    }

}