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

package de.dreier.mytargets.base.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.base.gallery.adapters.HorizontalListAdapters
import de.dreier.mytargets.base.gallery.adapters.ViewPagerAdapter
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.databinding.ActivityGalleryBinding
import de.dreier.mytargets.utils.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File
import java.io.IOException
import java.util.*

@RuntimePermissions
class GalleryActivity : ChildActivityBase() {

    internal var adapter: ViewPagerAdapter? = null
    internal var layoutManager: LinearLayoutManager? = null
    internal lateinit var previewAdapter: HorizontalListAdapters

    @State
    lateinit var imageList: ImageList

    private lateinit var binding: ActivityGalleryBinding

    private lateinit var easyImage: EasyImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)

        val title = intent.getStringExtra(EXTRA_TITLE)
        if (savedInstanceState == null) {
            imageList = intent.getParcelableExtra(EXTRA_IMAGES)
        }

        setSupportActionBar(binding.toolbar)

        ToolbarUtils.showHomeAsUp(this)
        ToolbarUtils.setTitle(this, title)
        Utils.showSystemUI(this)

        easyImage = EasyImage.Builder(this).build()

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imagesHorizontalList.layoutManager = layoutManager

        adapter = ViewPagerAdapter(this, imageList, binding.toolbar, binding.imagesHorizontalList)
        binding.pager.adapter = adapter

        previewAdapter = HorizontalListAdapters(this, imageList, { this.goToImage(it) })
        binding.imagesHorizontalList.adapter = previewAdapter
        previewAdapter.notifyDataSetChanged()

        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.imagesHorizontalList.smoothScrollToPosition(position)
                previewAdapter.setSelectedItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        val currentPos = 0
        previewAdapter.setSelectedItem(currentPos)
        binding.pager.currentItem = currentPos

        if (imageList.size() == 0 && savedInstanceState == null) {
            onTakePictureWithPermissionCheck()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gallery, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_share).isVisible = !imageList.isEmpty
        menu.findItem(R.id.action_delete).isVisible = !imageList.isEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                val currentItem = binding.pager.currentItem
                shareImage(currentItem)
                return true
            }
            R.id.action_delete -> {
                val currentItem = binding.pager.currentItem
                deleteImage(currentItem)
                return true
            }
            android.R.id.home -> {
                navigationController.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun shareImage(currentItem: Int) {
        val currentImage = imageList[currentItem]
        val file = File(filesDir, currentImage.fileName)
        val uri = file.toUri(this)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "*/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun deleteImage(currentItem: Int) {
        MaterialDialog.Builder(this)
            .content(R.string.delete_image)
            .negativeText(android.R.string.cancel)
            .negativeColorRes(R.color.md_grey_500)
            .positiveText(R.string.delete)
            .positiveColorRes(R.color.md_red_500)
            .onPositive { _, _ ->
                imageList.remove(currentItem)
                updateResult()
                invalidateOptionsMenu()
                adapter!!.notifyDataSetChanged()
                val nextItem = Math.min(imageList.size() - 1, currentItem)
                previewAdapter.setSelectedItem(nextItem)
                binding.pager.currentItem = nextItem
            }
            .show()
    }

    private fun updateResult() {
        navigationController.setResultSuccess(imageList)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    internal fun onTakePicture() {
        easyImage.openCameraForImage(this)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onSelectImage() {
        easyImage.openGallery(this)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, this,
            object : DefaultCallback() {

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    loadImages(imageFiles.map { it.file })
                }
            })
    }

    private fun loadImages(imageFile: List<File>) {
        object : AsyncTask<Void, Void, List<String>>() {

            override fun doInBackground(vararg params: Void): List<String> {
                val internalFiles = ArrayList<String>()
                for (file in imageFile) {
                    try {
                        val internal = File.createTempFile("img", file.name, filesDir)
                        internalFiles.add(internal.name)
                        file.moveTo(internal)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                return internalFiles
            }

            override fun onPostExecute(files: List<String>) {
                super.onPostExecute(files)
                imageList.addAll(files)
                updateResult()
                invalidateOptionsMenu()
                previewAdapter.notifyDataSetChanged()
                adapter!!.notifyDataSetChanged()
                val currentPos = imageList.size() - 1
                previewAdapter.setSelectedItem(currentPos)
                binding.pager.currentItem = currentPos
            }
        }.execute()
    }

    private fun goToImage(pos: Int) {
        if (imageList.size() == pos) {
            onTakePictureWithPermissionCheck()
        } else {
            binding.pager.setCurrentItem(pos, true)
        }
    }

    companion object {
        const val EXTRA_IMAGES = "images"
        const val EXTRA_TITLE = "title"

        fun getResult(data: Intent): ImageList {
            return data.getParcelableExtra(NavigationController.ITEM)
        }
    }
}
