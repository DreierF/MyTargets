package de.dreier.mytargets.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
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
import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ChipsViewBinding;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;
import icepick.Icepick;

public class ChipGroup extends ViewGroup {
    private final int default_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_background_color = Color.WHITE;
    private final int default_checked_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_checked_text_color = Color.WHITE;
    private final int default_checked_background_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_pressed_background_color = Color.rgb(0xED, 0xED, 0xED);
    private final float default_border_stroke_width;
    private final float default_text_size;
    private final float default_horizontal_spacing;
    private final float default_vertical_spacing;
    private final float default_horizontal_padding;
    private final float default_vertical_padding;

    ArrayList<Tag> tagList = new ArrayList<>();

    /**
     * The tag outline border color.
     */
    private int borderColor;

    /**
     * The tag text color.
     */
    private int textColor;

    /**
     * The tag background color.
     */
    private int backgroundColor;

    /**
     * The checked tag outline border color.
     */
    private int checkedBorderColor;

    /**
     * The check text color
     */
    private int checkedTextColor;

    /**
     * The checked tag background color.
     */
    private int checkedBackgroundColor;

    /**
     * The tag background color, when the tag is being pressed.
     */
    private int pressedBackgroundColor;

    /**
     * The tag outline border stroke width, default is 0.5dp.
     */
    private float borderStrokeWidth;

    /**
     * The tag text size, default is 13sp.
     */
    private float textSize;

    /**
     * The horizontal tag spacing, default is 8.0dp.
     */
    private int horizontalSpacing;

    /**
     * The vertical tag spacing, default is 4.0dp.
     */
    private int verticalSpacing;

    /**
     * The horizontal tag padding, default is 12.0dp.
     */
    private int horizontalPadding;

    /**
     * The vertical tag padding, default is 3.0dp.
     */
    private int verticalPadding;

    /**
     * Listener used to dispatch tag click event.
     */
    private OnTagClickListener mOnTagClickListener;

    public ChipGroup(Context context) {
        this(context, null);
    }

    public ChipGroup(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tagGroupStyle);
    }

    public ChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_border_stroke_width = dp2px(0.5f);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(12.0f);
        default_vertical_padding = dp2px(3.0f);

        // Load styled attributes.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChipGroup, defStyleAttr, R.style.ChipGroup);
        try {
            borderColor = a.getColor(R.styleable.ChipGroup_atg_borderColor, default_border_color);
            textColor = a.getColor(R.styleable.ChipGroup_atg_textColor, default_text_color);
            backgroundColor = a.getColor(R.styleable.ChipGroup_atg_backgroundColor, default_background_color);
            checkedBorderColor = a.getColor(R.styleable.ChipGroup_atg_checkedBorderColor, default_checked_border_color);
            checkedTextColor = a.getColor(R.styleable.ChipGroup_atg_checkedTextColor, default_checked_text_color);
            checkedBackgroundColor = a.getColor(R.styleable.ChipGroup_atg_checkedBackgroundColor, default_checked_background_color);
            pressedBackgroundColor = a.getColor(R.styleable.ChipGroup_atg_pressedBackgroundColor, default_pressed_background_color);
            borderStrokeWidth = a.getDimension(R.styleable.ChipGroup_atg_borderStrokeWidth, default_border_stroke_width);
            textSize = a.getDimension(R.styleable.ChipGroup_atg_textSize, default_text_size);
            horizontalSpacing = (int) a.getDimension(R.styleable.ChipGroup_atg_horizontalSpacing, default_horizontal_spacing);
            verticalSpacing = (int) a.getDimension(R.styleable.ChipGroup_atg_verticalSpacing, default_vertical_spacing);
            horizontalPadding = (int) a.getDimension(R.styleable.ChipGroup_atg_horizontalPadding, default_horizontal_padding);
            verticalPadding = (int) a.getDimension(R.styleable.ChipGroup_atg_verticalPadding, default_vertical_padding);
        } finally {
            a.recycle();
        }
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
     * Returns the tag list in group.
     *
     * @return the tag list.
     */
    public List<Tag> getTags() {
        return tagList;
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(Tag... tags) {
        setTags(Arrays.asList(tags));
    }

    /**
     * @see #setTags(Tag...)
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
            v.setActivated(tag.isChecked);
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(tagList);
            }
        });
        addView(binding.getRoot());
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
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
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag text of the tag that was clicked.
         */
        void onTagClick(List<Tag> tag);
    }

    /**
     * Per-child layout information for layouts.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
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
            binding.getRoot().setActivated(isChecked);
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