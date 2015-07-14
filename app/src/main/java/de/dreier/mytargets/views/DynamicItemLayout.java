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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;

public class DynamicItemLayout<T> extends LinearLayout implements View.OnClickListener {


    public interface OnBindListener<T> {
        void onBind(View view, T t, int index);
    }

    private int layoutResource;
    private OnBindListener<T> listener;
    private Class<T> clazz;
    private boolean rebind = false;
    private ArrayList<T> list = new ArrayList<>();

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
                R.layout.add_button, this, true);
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
        final int index = list.indexOf(item);
        list.remove(index);
        removeViewAt(index);
        rebindViews(index);

        Snackbar.make(this, R.string.sight_setting_removed,
                Snackbar.LENGTH_LONG)
                .setAction(undoStringRes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItem(index, item);
                    }
                }).show();
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

    public void addItem(int index, T item) {
        inflateView(index, item);
        rebindViews(index + 1);
    }

    private void inflateView(int index, T item) {
        View view = LayoutInflater.from(getContext()).inflate(
                layoutResource, this, false);
        listener.onBind(view, item, index);
        addView(view, index);
        list.add(index, item);
    }
}
