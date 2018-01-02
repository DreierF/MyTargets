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

package de.dreier.mytargets.views.speeddial

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IdRes
import android.support.design.internal.NavigationMenu
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.view.SupportMenuInflater
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.util.AndroidRuntimeException
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import de.dreier.mytargets.R
import timber.log.Timber
import java.util.*


typealias MenuListener = (menuItem: MenuItem) -> Boolean

/**
 * Adopted from https://github.com/yavski/fab-speed-dial
 */
@SuppressLint("RestrictedApi")
@CoordinatorLayout.DefaultBehavior(FabSpeedDialBehaviour::class)
class FabSpeedDial : LinearLayout, View.OnClickListener {

    private var menuListener: MenuListener? = null
    private var closeListener: CloseListener? = null
    private var navigationMenu: NavigationMenu? = null
    private var fabMenuItemMap: MutableMap<FloatingActionButton, MenuItem>? = null
    private var cardViewMenuItemMap: MutableMap<CardView, MenuItem>? = null

    private var menuItemsLayout: LinearLayout? = null
    internal lateinit var fab: FloatingActionButton
    private var touchGuard: View? = null

    private var menuId: Int = 0
    private var fabDrawable: Drawable? = null
    private var fabDrawableTint: ColorStateList? = null
    private var fabBackgroundTint: ColorStateList? = null
    private var miniFabDrawableTint: ColorStateList? = null
    private var miniFabBackgroundTint: ColorStateList? = null
    private var miniFabBackgroundTintArray: IntArray? = null
    private var miniFabTitleBackgroundTint: ColorStateList? = null
    private var miniFabTitlesEnabled: Boolean = false
    private var miniFabTitleTextColor: Int = 0
    private var miniFabTitleTextColorArray: IntArray? = null
    private var touchGuardDrawable: Drawable? = null
    private var useTouchGuard: Boolean = false

    private var isAnimating: Boolean = false

    // Variable to hold whether the menu was open or not on config change
    private var shouldOpenMenu: Boolean = false

    val isMenuOpen: Boolean
        get() = menuItemsLayout!!.childCount > 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.FabSpeedDial, 0, 0)
        resolveCompulsoryAttributes(typedArray)
        resolveOptionalAttributes(typedArray)
        typedArray.recycle()

        LayoutInflater.from(context).inflate(R.layout.fab_speed_dial_bottom, this, true)
        gravity = Gravity.END

        menuItemsLayout = findViewById(R.id.menu_items_layout)

        orientation = LinearLayout.VERTICAL

        newNavigationMenu()

        val menuItemCount = navigationMenu!!.size()
        fabMenuItemMap = HashMap(menuItemCount)
        cardViewMenuItemMap = HashMap(menuItemCount)
    }

    private fun resolveCompulsoryAttributes(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.FabSpeedDial_fabMenu)) {
            menuId = typedArray.getResourceId(R.styleable.FabSpeedDial_fabMenu, 0)
        } else {
            throw AndroidRuntimeException("You must provide the id of the menu resource.")
        }
    }

    private fun resolveOptionalAttributes(typedArray: TypedArray) {
        fabDrawable = typedArray.getDrawable(R.styleable.FabSpeedDial_fabDrawable)
        if (fabDrawable == null) {
            fabDrawable = ContextCompat.getDrawable(context, R.drawable.fab_buttonstates)
        }

        fabDrawableTint = typedArray.getColorStateList(R.styleable.FabSpeedDial_fabDrawableTint)
        if (fabDrawableTint == null) {
            fabDrawableTint = getColorStateList(R.color.fab_drawable_tint)
        }

        if (typedArray.hasValue(R.styleable.FabSpeedDial_fabBackgroundTint)) {
            fabBackgroundTint = typedArray
                    .getColorStateList(R.styleable.FabSpeedDial_fabBackgroundTint)
        }

        miniFabBackgroundTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabBackgroundTint)
        if (miniFabBackgroundTint == null) {
            miniFabBackgroundTint = getColorStateList(R.color.fab_background_tint)
        }

        if (typedArray.hasValue(R.styleable.FabSpeedDial_miniFabBackgroundTintList)) {
            val miniFabBackgroundTintListId = typedArray
                    .getResourceId(R.styleable.FabSpeedDial_miniFabBackgroundTintList, 0)
            val miniFabBackgroundTintRes = resources
                    .obtainTypedArray(miniFabBackgroundTintListId)
            miniFabBackgroundTintArray = IntArray(miniFabBackgroundTintRes.length())
            for (i in 0 until miniFabBackgroundTintRes.length()) {
                miniFabBackgroundTintArray!![i] = miniFabBackgroundTintRes.getResourceId(i, 0)
            }
            miniFabBackgroundTintRes.recycle()
        }

        miniFabDrawableTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabDrawableTint)
        if (miniFabDrawableTint == null) {
            miniFabDrawableTint = getColorStateList(R.color.mini_fab_drawable_tint)
        }

        miniFabTitleBackgroundTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabTitleBackgroundTint)
        if (miniFabTitleBackgroundTint == null) {
            miniFabTitleBackgroundTint = getColorStateList(R.color.mini_fab_title_background_tint)
        }

        miniFabTitlesEnabled = typedArray
                .getBoolean(R.styleable.FabSpeedDial_miniFabTitlesEnabled, true)


        miniFabTitleTextColor = typedArray.getColor(R.styleable.FabSpeedDial_miniFabTitleTextColor,
                ContextCompat.getColor(context, R.color.title_text_color))

        if (typedArray.hasValue(R.styleable.FabSpeedDial_miniFabTitleTextColorList)) {
            val miniFabTitleTextColorListId = typedArray
                    .getResourceId(R.styleable.FabSpeedDial_miniFabTitleTextColorList, 0)
            val miniFabTitleTextColorTa = resources
                    .obtainTypedArray(miniFabTitleTextColorListId)
            miniFabTitleTextColorArray = IntArray(miniFabTitleTextColorTa.length())
            for (i in 0 until miniFabTitleTextColorTa.length()) {
                miniFabTitleTextColorArray!![i] = miniFabTitleTextColorTa.getResourceId(i, 0)
            }
            miniFabTitleTextColorTa.recycle()
        }

        touchGuardDrawable = typedArray.getDrawable(R.styleable.FabSpeedDial_touchGuardDrawable)

        useTouchGuard = typedArray.getBoolean(R.styleable.FabSpeedDial_touchGuard, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val coordinatorLayoutOffset = resources
                .getDimensionPixelSize(R.dimen.coordinator_layout_offset)
        layoutParams.setMargins(0, 0, coordinatorLayoutOffset, 0)
        menuItemsLayout!!.layoutParams = layoutParams

        // Set up the client's FAB
        fab = findViewById(R.id.fab)
        fab.setImageDrawable(fabDrawable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.imageTintList = fabDrawableTint
        }
        if (fabBackgroundTint != null) {
            fab.backgroundTintList = fabBackgroundTint
        }

        fab.setOnClickListener {
            if (isAnimating) {
                return@setOnClickListener
            }

            if (isMenuOpen) {
                closeMenu()
            } else {
                openMenu()
            }
        }

        // Needed in order to intercept key events
        isFocusableInTouchMode = true

        if (useTouchGuard) {
            val parent = parent

            touchGuard = View(context)
            touchGuard!!.setOnClickListener(this)
            touchGuard!!.setWillNotDraw(true)
            touchGuard!!.visibility = View.GONE

            if (touchGuardDrawable != null) {
                touchGuard!!.background = touchGuardDrawable
            }

            if (parent is FrameLayout) {
                parent.addView(touchGuard)
                bringToFront()
            } else if (parent is CoordinatorLayout) {
                parent.addView(touchGuard)
                bringToFront()
            } else if (parent is RelativeLayout) {
                parent.addView(touchGuard,
                        RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT))
                bringToFront()
            } else {
                Timber.d("touchGuard requires that the parent of this FabSpeedDialer be a FrameLayout or RelativeLayout")
            }
        }

        setOnClickListener(this)

        if (shouldOpenMenu) {
            openMenu()
        }
    }

    fun getFabFromMenuId(@IdRes id: Int): FloatingActionButton {
        return fabMenuItemMap!!.entries
                .filter { entry -> entry.value.itemId == id }
                .map { it.key }
                .first()
    }

    private fun newNavigationMenu() {
        navigationMenu = NavigationMenu(context)
        SupportMenuInflater(context).inflate(menuId, navigationMenu)

        navigationMenu!!.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return menuListener?.invoke(item) ?: false
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })
    }

    override fun onClick(v: View) {
        fab.isSelected = false
        removeFabMenuItems()

        if (menuListener != null) {
            if (v !== this && v !== touchGuard) {
                if (v is FloatingActionButton) {
                    menuListener!!.invoke(fabMenuItemMap!![v]!!)
                } else if (v is CardView) {
                    menuListener!!.invoke(cardViewMenuItemMap!![v]!!)
                }
            }
        } else {
            Timber.d("You haven't provided a MenuListener.")
        }

        if (closeListener != null && (v === this || v === touchGuard)) {
            closeListener!!.onMenuClosed()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        ss.isShowingMenu = isMenuOpen

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)

        this.shouldOpenMenu = state.isShowingMenu
    }

    fun setMenuListener(menuListener: MenuListener) {
        this.menuListener = menuListener
    }

    fun setCloseListener(closeListener: CloseListener) {
        this.closeListener = closeListener
    }

    fun openMenu() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return
        }
        requestFocus()

        addMenuItems()
        fab.isSelected = true
        val fabDrawable = fab.drawable
        if (fabDrawable.current is Animatable) {
            (fabDrawable.current as Animatable).start()
        }
    }

    fun closeMenu() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return
        }

        if (isMenuOpen) {
            fab.isSelected = false
            val fabDrawable = fab.drawable
            if (fabDrawable.current is Animatable) {
                (fabDrawable.current as Animatable).start()
            }
            removeFabMenuItems()
            if (closeListener != null) {
                closeListener!!.onMenuClosed()
            }
        }
    }

    fun show() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return
        }
        visibility = View.VISIBLE
        fab.show()
    }

    fun hide() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return
        }

        if (isMenuOpen) {
            closeMenu()
        }
        fab.hide()
    }

    private fun addMenuItems() {
        menuItemsLayout!!.alpha = 1f
        for (i in 0 until navigationMenu!!.size()) {
            val menuItem = navigationMenu!!.getItem(i)
            if (menuItem.isVisible) {
                menuItemsLayout!!.addView(createFabMenuItem(menuItem))
            }
        }
        animateFabMenuItemsIn()
    }

    private fun createFabMenuItem(menuItem: MenuItem): View {
        val fabMenuItem = LayoutInflater.from(context)
                .inflate(R.layout.fab_menu_item_end, this, false) as ViewGroup

        val miniFab = fabMenuItem.findViewById<FloatingActionButton>(R.id.mini_fab)
        val cardView = fabMenuItem.findViewById<CardView>(R.id.card_view)
        val titleView = fabMenuItem.findViewById<TextView>(R.id.title_view)

        fabMenuItemMap!!.put(miniFab, menuItem)
        cardViewMenuItemMap!!.put(cardView, menuItem)

        miniFab.setImageDrawable(menuItem.icon)
        miniFab.setOnClickListener(this)
        cardView.setOnClickListener(this)

        miniFab.alpha = 0f
        cardView.alpha = 0f

        val title = menuItem.title
        if (!TextUtils.isEmpty(title) && miniFabTitlesEnabled) {
            cardView.setCardBackgroundColor(miniFabTitleBackgroundTint!!.defaultColor)
            titleView.text = title
            titleView.setTypeface(null, Typeface.BOLD)
            titleView.setTextColor(miniFabTitleTextColor)

            if (miniFabTitleTextColorArray != null) {
                titleView.setTextColor(ContextCompat.getColorStateList(context,
                        miniFabTitleTextColorArray!![menuItem.order]))
            }
        } else {
            fabMenuItem.removeView(cardView)
        }

        miniFab.backgroundTintList = miniFabBackgroundTint

        if (miniFabBackgroundTintArray != null) {
            miniFab.backgroundTintList = ContextCompat.getColorStateList(context,
                    miniFabBackgroundTintArray!![menuItem.order])
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniFab.imageTintList = miniFabDrawableTint
        }

        return fabMenuItem
    }

    private fun removeFabMenuItems() {
        if (touchGuard != null) {
            touchGuard!!.visibility = View.GONE
        }

        ViewCompat.animate(menuItemsLayout)
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .alpha(0f)
                .setInterpolator(FastOutLinearInInterpolator())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationStart(view: View?) {
                        super.onAnimationStart(view)
                        isAnimating = true
                    }

                    override fun onAnimationEnd(view: View?) {
                        super.onAnimationEnd(view)
                        menuItemsLayout!!.removeAllViews()
                        isAnimating = false
                    }
                })
                .start()
    }

    private fun animateFabMenuItemsIn() {
        if (touchGuard != null) {
            touchGuard!!.visibility = View.VISIBLE
        }

        val count = menuItemsLayout!!.childCount

        for (i in count - 1 downTo 0) {
            val fabMenuItem = menuItemsLayout!!.getChildAt(i)
            animateViewIn(fabMenuItem.findViewById(R.id.mini_fab), Math.abs(count - 1 - i))
            val cardView = fabMenuItem.findViewById<View>(R.id.card_view)
            if (cardView != null) {
                animateViewIn(cardView, Math.abs(count - 1 - i))
            }
        }
    }

    private fun animateViewIn(view: View, position: Int) {
        val offsetY = resources.getDimensionPixelSize(R.dimen.keyline_1).toFloat()

        view.scaleX = 0.25f
        view.scaleY = 0.25f
        view.y = view.y + offsetY

        ViewCompat.animate(view)
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .scaleX(1f)
                .scaleY(1f)
                .translationYBy(-offsetY)
                .alpha(1f)
                .setStartDelay((4 * position * VSYNC_RHYTHM).toLong())
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationStart(view: View?) {
                        super.onAnimationStart(view)
                        isAnimating = true
                    }

                    override fun onAnimationEnd(view: View?) {
                        super.onAnimationEnd(view)
                        isAnimating = false
                    }
                })
                .start()
    }

    private fun getColorStateList(colorRes: Int): ColorStateList {
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled), // enabled
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_pressed)  // pressed
        )

        val color = ContextCompat.getColor(context, colorRes)

        val colors = intArrayOf(color, color, color, color)
        return ColorStateList(states, colors)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (isMenuOpen
                && event.keyCode == KeyEvent.KEYCODE_BACK
                && event.action == KeyEvent.ACTION_UP
                && event.repeatCount == 0) {
            closeMenu()
            return true
        }

        return super.dispatchKeyEventPreIme(event)
    }

    internal class SavedState : View.BaseSavedState {

        var isShowingMenu: Boolean = false

        constructor(source: Parcel) : super(source) {
            this.isShowingMenu = source.readInt() == 1
        }

        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(if (this.isShowingMenu) 1 else 0)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(i: Int): Array<SavedState> {
                    @Suppress("UNCHECKED_CAST")
                    return arrayOfNulls<SavedState>(i) as Array<SavedState>
                }
            }
        }

    }

    interface CloseListener {
        fun onMenuClosed()
    }

    companion object {
        private const val VSYNC_RHYTHM = 16
    }
}
