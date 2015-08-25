/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;

public class DynamicItemLayout<T> extends LinearLayout implements View.OnClickListener {


    public interface OnBindListener<T> {
        void onBind(View view, T t, int index);
    }

    private Class<T> clazz;
    private int layoutResource;
    private boolean rebind = false;
    private OnBindListener<T> listener;
    private final ArrayList<T> list = new ArrayList<>();

    public DynamicItemLayout(Context context) {
        super(context);
        init();
    }

    public DynamicItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(
                R.layout.layout_add_button, this, true);
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
    }

    public void setLayoutResource(@LayoutRes int layout, Class<T> clazz) {
        layoutResource = layout;
        this.clazz = clazz;
    }

    public void setOnBindListener(OnBindListener<T> listener) {
        this.listener = listener;
    }

    public void rebindOnIndexChanged(boolean rebind) {
        this.rebind = rebind;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        for (T item : list) {
            inflateView(this.list.size(), item);
        }
    }

    public void remove(final T item, @StringRes int undoStringRes) {
        int i = 0;
        for (; i < list.size(); i++) {
            if (list.get(i) == item) {
                break;
            }
        }
        final int index = i;
        list.remove(index);
        removeViewAt(index);
        rebindViews(index);

        Snackbar.make(this, undoStringRes,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> { addItem(index, item); }).show();
    }

    private void rebindViews(int index) {
        if (rebind) {
            for (; index < list.size(); index++) {
                listener.onBind(getChildAt(index), list.get(index), index);
            }
        }
    }

    @Override
    public void onClick(View v) {
        T item;
        try {
            item = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        inflateView(list.size(), item);
    }

    public void addItem(T item) {
        inflateView(list.size(), item);
    }

    private void addItem(int index, T item) {
        inflateView(index, item);
        rebindViews(index + 1);
    }

    private void inflateView(int index, T item) {
        View view = LayoutInflater.from(getContext()).inflate(
                layoutResource, this, false);
        listener.onBind(view, item, index);
        addView(view, index);
        list.add(index, item);
        ViewParent container = getParent();
        while (container != null) {
            if (container instanceof NestedScrollView) {
                final NestedScrollView scrollView = (NestedScrollView) container;
                scrollView.postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 200);
                break;
            }
            container = container.getParent();
        }
    }
}
