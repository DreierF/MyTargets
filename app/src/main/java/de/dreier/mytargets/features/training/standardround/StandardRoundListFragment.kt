/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.training.standardround

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.collection.LongSparseArray
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.adapters.header.HeaderListAdapter
import de.dreier.mytargets.base.db.StandardRoundFactory
import de.dreier.mytargets.base.fragments.LoaderUICallback
import de.dreier.mytargets.base.fragments.SelectItemFragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.databinding.FragmentListBinding
import de.dreier.mytargets.databinding.ItemStandardRoundBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.utils.SlideInItemAnimator
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.contains
import de.dreier.mytargets.utils.multiselector.OnItemLongClickListener
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

class StandardRoundListFragment :
    SelectItemFragmentBase<AugmentedStandardRound, HeaderListAdapter<AugmentedStandardRound>>(),
    SearchView.OnQueryTextListener, OnItemLongClickListener<AugmentedStandardRound> {

    @State
    var currentSelection: AugmentedStandardRound? = null
    private var searchView: SearchView? = null

    private lateinit var binding: FragmentListBinding

    private val standardRoundDAO = ApplicationInstance.db.standardRoundDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            currentSelection = arguments!!.getParcelable(ITEM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = SlideInItemAnimator()
        val usedRounds = SettingsManager.standardRoundsLastUsed
        adapter = StandardRoundListAdapter(context!!, usedRounds)
        binding.recyclerView.adapter = adapter
        binding.fab.visibility = View.GONE
        ToolbarUtils.showUpAsX(this)
        binding.fab.visibility = View.VISIBLE
        binding.fab.setOnClickListener {
            navigationController.navigateToCreateStandardRound()
                .fromFab(binding.fab).forResult(NEW_STANDARD_ROUND)
                .start()
        }
        useDoubleClickSelection = true
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onLoad(args: Bundle?): LoaderUICallback {
        val data = if (args != null && args.containsKey(KEY_QUERY)) {
            val query = args.getString(KEY_QUERY)
            standardRoundDAO.getAllSearch("%${query!!.replace(' ', '%')}%")
        } else {
            standardRoundDAO.loadStandardRounds()
        }.map {
            AugmentedStandardRound(
                it,
                standardRoundDAO.loadRoundTemplates(it.id).toMutableList()
            )
        }
        return {
            adapter.setList(data)
            selectItem(binding.recyclerView, currentSelection!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (searchView != null) {
            val args = Bundle()
            args.putString(KEY_QUERY, searchView!!.query.toString())
            reloadData(args)
        } else {
            reloadData()
        }
    }

    override fun onClick(
        holder: SelectableViewHolder<AugmentedStandardRound>,
        item: AugmentedStandardRound?
    ) {
        currentSelection = item
        super.onClick(holder, item)
    }

    override fun onLongClick(holder: SelectableViewHolder<AugmentedStandardRound>) {
        val item = holder.item!!
        if (item.standardRound.club == StandardRoundFactory.CUSTOM) {
            navigationController.navigateToEditStandardRound(item)
                .forResult(EDIT_STANDARD_ROUND)
                .start()
        } else {
            MaterialDialog.Builder(context!!)
                .title(R.string.use_as_template)
                .content(R.string.create_copy)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ ->
                    navigationController
                        .navigateToEditStandardRound(item)
                        .forResult(NEW_STANDARD_ROUND)
                        .start()
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView!!.setOnQueryTextListener(this)
        val closeButton = searchView!!.findViewById<ImageView>(R.id.search_close_btn)
        // Set on click listener
        closeButton.setOnClickListener {
            val et = searchView!!.findViewById<EditText>(R.id.search_src_text)
            et.setText("")
            searchView!!.setQuery("", false)
            searchView!!.onActionViewCollapsed()
            searchItem.collapseActionView()
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        val args = Bundle()
        args.putString(KEY_QUERY, query)
        reloadData(args)
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == NEW_STANDARD_ROUND && data != null) {
            persistSelection(data.getParcelableExtra(ITEM))
            navigationController.setResultSuccess(data)
            navigationController.finish()
        } else if (requestCode == EDIT_STANDARD_ROUND && data != null) {
            if (resultCode == RESULT_OK) {
                currentSelection = data.getParcelableExtra(ITEM)
                reloadData()
            } else if (resultCode == EditStandardRoundFragment.RESULT_STANDARD_ROUND_DELETED) {
                currentSelection = standardRoundDAO.loadAugmentedStandardRound(32L)
                saveItem()
                reloadData()
            }
        }
    }

    override fun onSave(): AugmentedStandardRound {
        persistSelection(currentSelection!!)
        return currentSelection!!
    }

    private fun persistSelection(standardRound: AugmentedStandardRound) {
        val map = SettingsManager.standardRoundsLastUsed
        val counter = map.get(standardRound.id)
        if (counter == null) {
            map.put(standardRound.id, 1)
        } else {
            map.put(standardRound.id, counter + 1)
        }
        SettingsManager.standardRoundsLastUsed = map
    }

    private inner class StandardRoundListAdapter internal constructor(
        context: Context,
        usedIds: LongSparseArray<Int>
    ) : HeaderListAdapter<AugmentedStandardRound>(
        { id ->
            if (usedIds.contains(id.id)) {
                HeaderListAdapter.SimpleHeader(0L, context.getString(R.string.recently_used))
            } else {
                HeaderListAdapter.SimpleHeader(1L, "")
            }
        },
        compareBy({ usedIds.get(it.standardRound.id) ?: 0 },
            { it.standardRound.name },
            { it.standardRound.id })
    ) {

        override fun getSecondLevelViewHolder(parent: ViewGroup): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_standard_round, parent, false)
            return ViewHolder(itemView)
        }
    }

    private inner class ViewHolder(itemView: View) : SelectableViewHolder<AugmentedStandardRound>(
        itemView,
        selector, this@StandardRoundListFragment, this@StandardRoundListFragment
    ) {

        private val binding = ItemStandardRoundBinding.bind(itemView)

        override fun bindItem(item: AugmentedStandardRound) {
            binding.name.text = item.standardRound.name

            if (item.id == currentSelection!!.id) {
                binding.image.visibility = View.VISIBLE
                binding.details.visibility = View.VISIBLE
                binding.details.text = item.getDescription(activity!!)
                binding.image.setImageDrawable(item.targetDrawable)
            } else {
                binding.image.visibility = View.GONE
                binding.details.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val NEW_STANDARD_ROUND = 1
        private const val EDIT_STANDARD_ROUND = 2
        private const val KEY_QUERY = "query"
    }
}
