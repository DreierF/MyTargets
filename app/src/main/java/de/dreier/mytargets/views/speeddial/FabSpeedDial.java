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

package de.dreier.mytargets.views.speeddial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.Map;

import de.dreier.mytargets.R;
import timber.log.Timber;

@SuppressLint("RestrictedApi")
@CoordinatorLayout.DefaultBehavior(FabSpeedDialBehaviour.class)
public class FabSpeedDial extends LinearLayout implements View.OnClickListener {

    /**
     * Called to notify of close and selection changes.
     */
    public interface MenuListener {

        /**
         * Called when a menu item is selected.
         *
         * @param menuItem The menu item that is selected
         * @return whether the menu item selection was handled
         */
        boolean onMenuItemSelected(MenuItem menuItem);
    }

    public interface CloseListener {
        void onMenuClosed();
    }

    private static final int VSYNC_RHYTHM = 16;

    private MenuListener menuListener;
    private CloseListener closeListener;
    private NavigationMenu navigationMenu;
    private Map<FloatingActionButton, MenuItem> fabMenuItemMap;
    private Map<CardView, MenuItem> cardViewMenuItemMap;

    private LinearLayout menuItemsLayout;
    FloatingActionButton fab;
    private View touchGuard = null;

    private int menuId;
    private Drawable fabDrawable;
    private ColorStateList fabDrawableTint;
    private ColorStateList fabBackgroundTint;
    private ColorStateList miniFabDrawableTint;
    private ColorStateList miniFabBackgroundTint;
    private int[] miniFabBackgroundTintArray;
    private ColorStateList miniFabTitleBackgroundTint;
    private boolean miniFabTitlesEnabled;
    private int miniFabTitleTextColor;
    private int[] miniFabTitleTextColorArray;
    private Drawable touchGuardDrawable;
    private boolean useTouchGuard;

    private boolean isAnimating;

    // Variable to hold whether the menu was open or not on config change
    private boolean shouldOpenMenu;

    public FabSpeedDial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FabSpeedDial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.FabSpeedDial, 0, 0);
        resolveCompulsoryAttributes(typedArray);
        resolveOptionalAttributes(typedArray);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.fab_speed_dial_bottom, this, true);
        setGravity(Gravity.END);

        menuItemsLayout = findViewById(R.id.menu_items_layout);

        setOrientation(VERTICAL);

        newNavigationMenu();

        int menuItemCount = navigationMenu.size();
        fabMenuItemMap = new HashMap<>(menuItemCount);
        cardViewMenuItemMap = new HashMap<>(menuItemCount);
    }

    private void resolveCompulsoryAttributes(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.FabSpeedDial_fabMenu)) {
            menuId = typedArray.getResourceId(R.styleable.FabSpeedDial_fabMenu, 0);
        } else {
            throw new AndroidRuntimeException("You must provide the id of the menu resource.");
        }
    }

    private void resolveOptionalAttributes(TypedArray typedArray) {
        fabDrawable = typedArray.getDrawable(R.styleable.FabSpeedDial_fabDrawable);
        if (fabDrawable == null) {
            fabDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fab_buttonstates);
        }

        fabDrawableTint = typedArray.getColorStateList(R.styleable.FabSpeedDial_fabDrawableTint);
        if (fabDrawableTint == null) {
            fabDrawableTint = getColorStateList(R.color.fab_drawable_tint);
        }

        if (typedArray.hasValue(R.styleable.FabSpeedDial_fabBackgroundTint)) {
            fabBackgroundTint = typedArray
                    .getColorStateList(R.styleable.FabSpeedDial_fabBackgroundTint);
        }

        miniFabBackgroundTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabBackgroundTint);
        if (miniFabBackgroundTint == null) {
            miniFabBackgroundTint = getColorStateList(R.color.fab_background_tint);
        }

        if (typedArray.hasValue(R.styleable.FabSpeedDial_miniFabBackgroundTintList)) {
            int miniFabBackgroundTintListId = typedArray
                    .getResourceId(R.styleable.FabSpeedDial_miniFabBackgroundTintList, 0);
            TypedArray miniFabBackgroundTintRes = getResources()
                    .obtainTypedArray(miniFabBackgroundTintListId);
            miniFabBackgroundTintArray = new int[miniFabBackgroundTintRes.length()];
            for (int i = 0; i < miniFabBackgroundTintRes.length(); i++) {
                miniFabBackgroundTintArray[i] = miniFabBackgroundTintRes.getResourceId(i, 0);
            }
            miniFabBackgroundTintRes.recycle();
        }

        miniFabDrawableTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabDrawableTint);
        if (miniFabDrawableTint == null) {
            miniFabDrawableTint = getColorStateList(R.color.mini_fab_drawable_tint);
        }

        miniFabTitleBackgroundTint = typedArray
                .getColorStateList(R.styleable.FabSpeedDial_miniFabTitleBackgroundTint);
        if (miniFabTitleBackgroundTint == null) {
            miniFabTitleBackgroundTint = getColorStateList(R.color.mini_fab_title_background_tint);
        }

        miniFabTitlesEnabled = typedArray
                .getBoolean(R.styleable.FabSpeedDial_miniFabTitlesEnabled, true);


        miniFabTitleTextColor = typedArray.getColor(R.styleable.FabSpeedDial_miniFabTitleTextColor,
                ContextCompat.getColor(getContext(), R.color.title_text_color));

        if (typedArray.hasValue(R.styleable.FabSpeedDial_miniFabTitleTextColorList)) {
            int miniFabTitleTextColorListId = typedArray
                    .getResourceId(R.styleable.FabSpeedDial_miniFabTitleTextColorList, 0);
            TypedArray miniFabTitleTextColorTa = getResources()
                    .obtainTypedArray(miniFabTitleTextColorListId);
            miniFabTitleTextColorArray = new int[miniFabTitleTextColorTa.length()];
            for (int i = 0; i < miniFabTitleTextColorTa.length(); i++) {
                miniFabTitleTextColorArray[i] = miniFabTitleTextColorTa.getResourceId(i, 0);
            }
            miniFabTitleTextColorTa.recycle();
        }

        touchGuardDrawable = typedArray.getDrawable(R.styleable.FabSpeedDial_touchGuardDrawable);

        useTouchGuard = typedArray.getBoolean(R.styleable.FabSpeedDial_touchGuard, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int coordinatorLayoutOffset = getResources()
                .getDimensionPixelSize(R.dimen.coordinator_layout_offset);
        layoutParams.setMargins(0, 0, coordinatorLayoutOffset, 0);
        menuItemsLayout.setLayoutParams(layoutParams);

        // Set up the client's FAB
        fab = findViewById(R.id.fab);
        fab.setImageDrawable(fabDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageTintList(fabDrawableTint);
        }
        if (fabBackgroundTint != null) {
            fab.setBackgroundTintList(fabBackgroundTint);
        }

        fab.setOnClickListener(v -> {
            if (isAnimating) {
                return;
            }

            if (isMenuOpen()) {
                closeMenu();
            } else {
                openMenu();
            }
        });

        // Needed in order to intercept key events
        setFocusableInTouchMode(true);

        if (useTouchGuard) {
            ViewParent parent = getParent();

            touchGuard = new View(getContext());
            touchGuard.setOnClickListener(this);
            touchGuard.setWillNotDraw(true);
            touchGuard.setVisibility(GONE);

            if (touchGuardDrawable != null) {
                touchGuard.setBackground(touchGuardDrawable);
            }

            if (parent instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) parent;
                frameLayout.addView(touchGuard);
                bringToFront();
            } else if (parent instanceof CoordinatorLayout) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) parent;
                coordinatorLayout.addView(touchGuard);
                bringToFront();
            } else if (parent instanceof RelativeLayout) {
                RelativeLayout relativeLayout = (RelativeLayout) parent;
                relativeLayout.addView(touchGuard,
                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                bringToFront();
            } else {
                Timber.d("touchGuard requires that the parent of this FabSpeedDialer be a FrameLayout or RelativeLayout");
            }
        }

        setOnClickListener(this);

        if (shouldOpenMenu) {
            openMenu();
        }
    }

    public FloatingActionButton getFabFromMenuId(@IdRes int id) {
        return Stream.of(fabMenuItemMap.entrySet())
                .filter(entry -> entry.getValue().getItemId() == id)
                .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    private void newNavigationMenu() {
        navigationMenu = new NavigationMenu(getContext());
        new SupportMenuInflater(getContext()).inflate(menuId, navigationMenu);

        navigationMenu.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                return menuListener != null && menuListener.onMenuItemSelected(item);
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        fab.setSelected(false);
        removeFabMenuItems();

        if (menuListener != null) {
            if (v != this && v != touchGuard) {
                if (v instanceof FloatingActionButton) {
                    menuListener.onMenuItemSelected(fabMenuItemMap.get(v));
                } else if (v instanceof CardView) {
                    menuListener.onMenuItemSelected(cardViewMenuItemMap.get(v));
                }
            }
        } else {
            Timber.d("You haven't provided a MenuListener.");
        }

        if (closeListener != null && (v == this || v == touchGuard)) {
            closeListener.onMenuClosed();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.isShowingMenu = isMenuOpen();

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.shouldOpenMenu = ss.isShowingMenu;
    }

    public void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public boolean isMenuOpen() {
        return menuItemsLayout.getChildCount() > 0;
    }

    public void openMenu() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return;
        }
        requestFocus();

        addMenuItems();
        fab.setSelected(true);
        Drawable fabDrawable = fab.getDrawable();
        if (fabDrawable.getCurrent() instanceof Animatable) {
            ((Animatable) fabDrawable.getCurrent()).start();
        }
    }

    public void closeMenu() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return;
        }

        if (isMenuOpen()) {
            fab.setSelected(false);
            Drawable fabDrawable = fab.getDrawable();
            if (fabDrawable.getCurrent() instanceof Animatable) {
                ((Animatable) fabDrawable.getCurrent()).start();
            }
            removeFabMenuItems();
            if (closeListener != null) {
                closeListener.onMenuClosed();
            }
        }
    }

    public void show() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return;
        }
        setVisibility(View.VISIBLE);
        fab.show();
    }

    public void hide() {
        if (!ViewCompat.isAttachedToWindow(this)) {
            return;
        }

        if (isMenuOpen()) {
            closeMenu();
        }
        fab.hide();
    }

    private void addMenuItems() {
        menuItemsLayout.setAlpha(1f);
        for (int i = 0; i < navigationMenu.size(); i++) {
            MenuItem menuItem = navigationMenu.getItem(i);
            if (menuItem.isVisible()) {
                menuItemsLayout.addView(createFabMenuItem(menuItem));
            }
        }
        animateFabMenuItemsIn();
    }

    private View createFabMenuItem(MenuItem menuItem) {
        ViewGroup fabMenuItem = (ViewGroup) LayoutInflater.from(getContext())
                .inflate(R.layout.fab_menu_item_end, this, false);

        FloatingActionButton miniFab = fabMenuItem.findViewById(R.id.mini_fab);
        CardView cardView = fabMenuItem.findViewById(R.id.card_view);
        TextView titleView = fabMenuItem.findViewById(R.id.title_view);

        fabMenuItemMap.put(miniFab, menuItem);
        cardViewMenuItemMap.put(cardView, menuItem);

        miniFab.setImageDrawable(menuItem.getIcon());
        miniFab.setOnClickListener(this);
        cardView.setOnClickListener(this);

        miniFab.setAlpha(0f);
        cardView.setAlpha(0f);

        final CharSequence title = menuItem.getTitle();
        if (!TextUtils.isEmpty(title) && miniFabTitlesEnabled) {
            cardView.setCardBackgroundColor(miniFabTitleBackgroundTint.getDefaultColor());
            titleView.setText(title);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setTextColor(miniFabTitleTextColor);

            if (miniFabTitleTextColorArray != null) {
                titleView.setTextColor(ContextCompat.getColorStateList(getContext(),
                        miniFabTitleTextColorArray[menuItem.getOrder()]));
            }
        } else {
            fabMenuItem.removeView(cardView);
        }

        miniFab.setBackgroundTintList(miniFabBackgroundTint);

        if (miniFabBackgroundTintArray != null) {
            miniFab.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                    miniFabBackgroundTintArray[menuItem.getOrder()]));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniFab.setImageTintList(miniFabDrawableTint);
        }

        return fabMenuItem;
    }

    private void removeFabMenuItems() {
        if (touchGuard != null) {
            touchGuard.setVisibility(GONE);
        }

        ViewCompat.animate(menuItemsLayout)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .alpha(0f)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        super.onAnimationStart(view);
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        menuItemsLayout.removeAllViews();
                        isAnimating = false;
                    }
                })
                .start();
    }

    private void animateFabMenuItemsIn() {
        if (touchGuard != null) {
            touchGuard.setVisibility(VISIBLE);
        }

        final int count = menuItemsLayout.getChildCount();

        for (int i = count - 1; i >= 0; i--) {
            final View fabMenuItem = menuItemsLayout.getChildAt(i);
            animateViewIn(fabMenuItem.findViewById(R.id.mini_fab), Math.abs(count - 1 - i));
            View cardView = fabMenuItem.findViewById(R.id.card_view);
            if (cardView != null) {
                animateViewIn(cardView, Math.abs(count - 1 - i));
            }
        }
    }

    private void animateViewIn(final View view, int position) {
        final float offsetY = getResources().getDimensionPixelSize(R.dimen.keyline_1);

        view.setScaleX(0.25f);
        view.setScaleY(0.25f);
        view.setY(view.getY() + offsetY);

        ViewCompat.animate(view)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .scaleX(1f)
                .scaleY(1f)
                .translationYBy(-offsetY)
                .alpha(1f)
                .setStartDelay(4 * position * VSYNC_RHYTHM)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        super.onAnimationStart(view);
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        isAnimating = false;
                    }
                })
                .start();
    }

    private ColorStateList getColorStateList(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int color = ContextCompat.getColor(getContext(), colorRes);

        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (isMenuOpen()
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP
                && event.getRepeatCount() == 0) {
            closeMenu();
            return true;
        }

        return super.dispatchKeyEventPreIme(event);
    }

    static class SavedState extends BaseSavedState {

        boolean isShowingMenu;

        public SavedState(Parcel source) {
            super(source);
            this.isShowingMenu = source.readInt() == 1;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.isShowingMenu ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };

    }

}
