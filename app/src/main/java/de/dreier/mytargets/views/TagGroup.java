package de.dreier.mytargets.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import icepick.Icepick;
import icepick.State;

public class TagGroup extends ViewGroup {
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

    /** The tag outline border color. */
    private int borderColor;

    /** The tag text color. */
    private int textColor;

    /** The tag background color. */
    private int backgroundColor;

    /** The checked tag outline border color. */
    private int checkedBorderColor;

    /** The check text color */
    private int checkedTextColor;

    /** The checked tag background color. */
    private int checkedBackgroundColor;

    /** The tag background color, when the tag is being pressed. */
    private int pressedBackgroundColor;

    /** The tag outline border stroke width, default is 0.5dp. */
    private float borderStrokeWidth;

    /** The tag text size, default is 13sp. */
    private float textSize;

    /** The horizontal tag spacing, default is 8.0dp. */
    private int horizontalSpacing;

    /** The vertical tag spacing, default is 4.0dp. */
    private int verticalSpacing;

    /** The horizontal tag padding, default is 12.0dp. */
    private int horizontalPadding;

    /** The vertical tag padding, default is 3.0dp. */
    private int verticalPadding;

    /** Listener used to dispatch tag click event. */
    private OnTagClickListener mOnTagClickListener;

    /** Listener used to handle tag click event. */
    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tagGroupStyle);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_border_stroke_width = dp2px(0.5f);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(12.0f);
        default_vertical_padding = dp2px(3.0f);

        // Load styled attributes.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagGroup, defStyleAttr, R.style.TagGroup);
        try {
            borderColor = a.getColor(R.styleable.TagGroup_atg_borderColor, default_border_color);
            textColor = a.getColor(R.styleable.TagGroup_atg_textColor, default_text_color);
            backgroundColor = a.getColor(R.styleable.TagGroup_atg_backgroundColor, default_background_color);
            checkedBorderColor = a.getColor(R.styleable.TagGroup_atg_checkedBorderColor, default_checked_border_color);
            checkedTextColor = a.getColor(R.styleable.TagGroup_atg_checkedTextColor, default_checked_text_color);
            checkedBackgroundColor = a.getColor(R.styleable.TagGroup_atg_checkedBackgroundColor, default_checked_background_color);
            pressedBackgroundColor = a.getColor(R.styleable.TagGroup_atg_pressedBackgroundColor, default_pressed_background_color);
            borderStrokeWidth = a.getDimension(R.styleable.TagGroup_atg_borderStrokeWidth, default_border_stroke_width);
            textSize = a.getDimension(R.styleable.TagGroup_atg_textSize, default_text_size);
            horizontalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalSpacing, default_horizontal_spacing);
            verticalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_verticalSpacing, default_vertical_spacing);
            horizontalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalPadding, default_horizontal_padding);
            verticalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_verticalPadding, default_vertical_padding);
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
        } else {// If the tags grouped exceed one line, set the width to match the parent.
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
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            tagList.add(tagView.getText().toString());
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(Tag... tags) {
        removeAllViews();
        for (final Tag tag : tags) {
            appendTag(tag);
        }
    }

    /**
     * @see #setTags(Tag...)
     */
    public void setTags(List<Tag> tagList) {
        setTags(tagList.toArray(new Tag[tagList.size()]));
    }

    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected TagView getTagAt(int index) {
        return (TagView) getChildAt(index);
    }

    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    protected void appendTag(Tag tag) {
        final TagView newTag = new TagView(getContext(), tag);
        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
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
        return new TagGroup.LayoutParams(getContext(), attrs);
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
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
        void onTagClick(String tag);
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

        public long id;

        public String text;

        /** Indicates the tag if checked. */
        public boolean isChecked = false;

        @ParcelConstructor
        public Tag(long id, String text, boolean isChecked) {
            this.id = id;
            this.text = text;
            this.isChecked = isChecked;
        }
    }

    /**
     * The tag view click listener for internal use.
     */
    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final TagView tv = (TagView) v;
            tv.setChecked(!tv.tag.isChecked);
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(tv.getText().toString());
            }
        }
    }

    /**
     * The tag view which has two states can be either NORMAL or INPUT.
     */
    class TagView extends TextView {

        @State(ParcelsBundler.class)
        Tag tag;

        /** Indicates the tag if pressed. */
        private boolean isPressed = false;

        private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /** The rect for the tag's left corner drawing. */
        private RectF mLeftCornerRectF = new RectF();

        /** The rect for the tag's right corner drawing. */
        private RectF mRightCornerRectF = new RectF();

        /** The rect for the tag's horizontal blank fill area. */
        private RectF mHorizontalBlankFillRectF = new RectF();

        /** The rect for the tag's vertical blank fill area. */
        private RectF mVerticalBlankFillRectF = new RectF();

        /** Used to detect the touch event. */
        private Rect mOutRect = new Rect();

        /** The path for draw the tag's outline border. */
        private Path mBorderPath = new Path();

        {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderStrokeWidth);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }


        public TagView(Context context, Tag tag) {
            super(context);
            this.tag = tag;
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            setLayoutParams(new TagGroup.LayoutParams(
                    TagGroup.LayoutParams.WRAP_CONTENT,
                    TagGroup.LayoutParams.WRAP_CONTENT));

            setGravity(Gravity.CENTER);
            setText(tag.text);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            setClickable(true);

            // Interrupted long click event to avoid PAUSE popup.
            setOnLongClickListener(v -> true);

            invalidatePaint();
        }

        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        public void setChecked(boolean checked) {
            tag.isChecked = checked;
            // Make the checked mark drawing region.
            invalidatePaint();
        }

        @Override
        protected boolean getDefaultEditable() {
            return true;
        }

        private void invalidatePaint() {
            mBorderPaint.setPathEffect(null);
            if (tag.isChecked) {
                mBorderPaint.setColor(checkedBorderColor);
                mBackgroundPaint.setColor(checkedBackgroundColor);
                setTextColor(checkedTextColor);
            } else {
                mBorderPaint.setColor(borderColor);
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }

            if (isPressed) {
                mBackgroundPaint.setColor(pressedBackgroundColor);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawArc(mLeftCornerRectF, -180, 90, true, mBackgroundPaint);
            canvas.drawArc(mLeftCornerRectF, -270, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, -90, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, 0, 90, true, mBackgroundPaint);
            canvas.drawRect(mHorizontalBlankFillRectF, mBackgroundPaint);
            canvas.drawRect(mVerticalBlankFillRectF, mBackgroundPaint);
            canvas.drawPath(mBorderPath, mBorderPaint);
            super.onDraw(canvas);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            int left = (int) borderStrokeWidth;
            int top = (int) borderStrokeWidth;
            int right = (int) (left + w - borderStrokeWidth * 2);
            int bottom = (int) (top + h - borderStrokeWidth * 2);

            int d = bottom - top;

            mLeftCornerRectF.set(left, top, left + d, top + d);
            mRightCornerRectF.set(right - d, top, right, top + d);

            mBorderPath.reset();
            mBorderPath.addArc(mLeftCornerRectF, -180, 90);
            mBorderPath.addArc(mLeftCornerRectF, -270, 90);
            mBorderPath.addArc(mRightCornerRectF, -90, 90);
            mBorderPath.addArc(mRightCornerRectF, 0, 90);

            int l = (int) (d / 2.0f);
            mBorderPath.moveTo(left + l, top);
            mBorderPath.lineTo(right - l, top);

            mBorderPath.moveTo(left + l, bottom);
            mBorderPath.lineTo(right - l, bottom);

            mBorderPath.moveTo(left, top + l);
            mBorderPath.lineTo(left, bottom - l);

            mBorderPath.moveTo(right, top + l);
            mBorderPath.lineTo(right, bottom - l);

            mHorizontalBlankFillRectF.set(left, top + l, right, bottom - l);
            mVerticalBlankFillRectF.set(left + l, top, right - l, bottom);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getDrawingRect(mOutRect);
                    isPressed = true;
                    invalidatePaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        isPressed = false;
                        invalidatePaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isPressed = false;
                    invalidatePaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }

        @Override
        public Parcelable onSaveInstanceState() {
            return Icepick.saveInstanceState(this, super.onSaveInstanceState());
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
            super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        }
    }
}