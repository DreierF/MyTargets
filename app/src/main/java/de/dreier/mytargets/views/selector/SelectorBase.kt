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

package de.dreier.mytargets.views.selector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ItemSelectActivity
import de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM
import de.dreier.mytargets.utils.IntentWrapper

typealias OnUpdateListener<T> = (T?) -> Unit

abstract class SelectorBase<T : Parcelable>(
        context: Context, attrs: AttributeSet?,
        @param:LayoutRes private val layout: Int) : LinearLayout(context, attrs) {

    protected lateinit var view: View
    protected var requestCode: Int = 0
    protected var defaultActivity: Class<*>? = null
    @State
    open var selectedItem: T? = null
    private var addButton: Button? = null
    private var progress: View? = null
    private var updateListener: OnUpdateListener<T>? = null
    private var index = -1
    private var addIntent: IntentWrapper? = null

    open protected val defaultIntent: IntentWrapper
        get() {
            val i = IntentWrapper(defaultActivity!!)
                    .with<T>(ITEM, selectedItem!!)
            if (index != -1) {
                i.with(INDEX, index)
            }
            return i
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addButton = getChildAt(0) as Button?
        addButton?.setOnClickListener { addIntent!!.start() }
        val inflater = LayoutInflater.from(context)
        progress = inflater.inflate(R.layout.selector_item_process, this, false)
        view = inflater.inflate(layout, this, false)
        addView(progress)
        addView(view)
        updateView()
    }

    private fun updateView() {
        val displayProgress = selectedItem == null && addButton == null
        addButton?.visibility = if (selectedItem == null) View.VISIBLE else View.GONE
        progress!!.visibility = if (displayProgress) View.VISIBLE else View.GONE
        view.visibility = if (selectedItem != null) View.VISIBLE else View.GONE
        selectedItem?.let { bindView(it) }
    }

    fun setItemIndex(index: Int) {
        this.index = index
    }

    protected open fun getAddIntent(): IntentWrapper? {
        return null
    }

    protected abstract fun bindView(item: T)

    fun setItem(item: T?) {
        this.selectedItem = item
        updateListener?.invoke(item)
        updateView()
    }

    fun setOnUpdateListener(updateListener: OnUpdateListener<T>) {
        this.updateListener = updateListener
    }

    fun setOnActivityResultContext(fragment: Fragment) {
        if (addButton != null) {
            addIntent = getAddIntent()!!.withContext(fragment)
        }
        setOnClickListener {
            defaultIntent
                    .withContext(fragment)
                    .forResult(requestCode)
                    .start()
        }
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode) {
            val intentData = data.getBundleExtra(ItemSelectActivity.INTENT)
            if (index == -1 || intentData != null && intentData.getInt(INDEX) == index) {
                setItem(data.getParcelableExtra(ITEM))
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState())
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state))
    }

    companion object {
        const val INDEX = "index"
    }
}
