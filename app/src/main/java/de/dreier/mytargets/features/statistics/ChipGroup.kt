/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.statistics

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import java.util.*

class ChipGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var tagList: MutableList<Tag> = ArrayList()

    private val horizontalSpacing = dp2px(8.0f).toInt()

    private val verticalSpacing = dp2px(4.0f).toInt()

    /**
     * Listener used to dispatch tag click event.
     */
    private var onSelectionChangedListener: ((Tag) -> Unit)? = null

    /**
     * Returns the tag list in group.
     *
     * @return the tag list.
     */
    /**
     * Set the tags. It will remove all previous tags first.
     * If the list of tags contains less than 2 elements the view sets its visibility to gone.
     *
     * @param tags the tag list to set.
     */
    var tags: List<Tag>
        get() = ArrayList(tagList)
        set(tags) {
            removeAllViews()
            tagList.clear()
            tagList.addAll(tags)
            for (tag in tagList) {
                appendTag(tag)
            }
            visibility = if (tags.size < 2) View.GONE else View.VISIBLE
        }

    /**
     * Returns the checked tag list in group.
     *
     * @return the tag list.
     */
    val checkedTags: List<Tag>
        get() = tagList.filter { it.isChecked }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var width: Int
        var height = 0

        var row = 0 // The row counter.
        var rowWidth = 0 // Calc the current row width.
        var rowMaxHeight = 0 // Calc the max tag height, in current row.

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (child.visibility != View.GONE) {
                rowWidth += childWidth
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth // The next row width.
                    height += rowMaxHeight + verticalSpacing
                    rowMaxHeight = childHeight // The next row max height.
                    row++
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight)
                }
                rowWidth += horizontalSpacing
            }
        }
        // Account for the last row height.
        height += rowMaxHeight

        // Account for the padding too.
        height += paddingTop + paddingBottom

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth
            width += paddingLeft + paddingRight
        } else { // If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize
        }

        setMeasuredDimension(if (widthMode == View.MeasureSpec.EXACTLY) widthSize else width,
                if (heightMode == View.MeasureSpec.EXACTLY) heightSize else height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentLeft = paddingLeft
        val parentRight = r - l - paddingRight
        val parentTop = paddingTop

        var childLeft = parentLeft
        var childTop = parentTop

        var rowMaxHeight = 0

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val width = child.measuredWidth
            val height = child.measuredHeight

            if (child.visibility != View.GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft
                    childTop += rowMaxHeight + verticalSpacing
                    rowMaxHeight = height
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height)
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height)

                childLeft += width + horizontalSpacing
            }
        }
    }

    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    private fun appendTag(tag: Tag) {
        val binding = tag.getView(context, this)
        binding.root.setOnClickListener { v ->
            tag.isChecked = !tag.isChecked
            binding.root.isActivated = !tag.isChecked
            onSelectionChangedListener?.invoke(tag)
        }
        addView(binding.root)
    }

    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.displayMetrics)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ChipGroup.LayoutParams(context, attrs)
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    fun setOnTagClickListener(l: (Tag) -> Unit) {
        onSelectionChangedListener = l
    }

    /**
     * Per-child layout information for layouts.
     */
    class LayoutParams(c: Context, attrs: AttributeSet) : ViewGroup.LayoutParams(c, attrs)

}
