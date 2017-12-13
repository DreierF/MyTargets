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

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.TextUtils
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Arrow(@Column(name = "_id")
                 @PrimaryKey(autoincrement = true)
                 internal var id: Long = 0,

                 @Column
                 override var name: String = "",

                 @Column
                 var maxArrowNumber: Int = 12,

                 @Column
                 var length: String? = "",

                 @Column
                 var material: String? = "",

                 @Column
                 var spine: String? = "",

                 @Column
                 var weight: String? = "",

                 @Column
                 var tipWeight: String? = "",

                 @Column
                 var vanes: String? = "",

                 @Column
                 var nock: String? = "",

                 @Column
                 var comment: String? = "",

                 @Column(typeConverter = DimensionConverter::class)
                 var diameter: Dimension = Dimension(5f, Dimension.Unit.MILLIMETER),

                 @Column(typeConverter = ThumbnailConverter::class)
                 @JvmField //DBFlow bug
                 var thumbnail: Thumbnail? = null) : BaseModel(), IImageProvider, IIdSettable, Comparable<Arrow>, IRecursiveModel, Parcelable {

    @Transient
    var images: List<ArrowImage>? = null

    val drawable: Drawable
        get() = thumbnail!!.roundDrawable

    @OneToMany(methods = [], variableName = "images")
    fun loadImages(): List<ArrowImage>? {
        if (images == null) {
            images = SQLite.select()
                    .from(ArrowImage::class.java)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .queryList()
        }
        return images
    }

    override fun getId(): Long? {
        return id
    }

    override fun setId(id: Long) {
        this.id = id
    }

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    override fun save() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { this.save(it) }
    }

    override fun save(databaseWrapper: DatabaseWrapper) {
        super.save(databaseWrapper)
        if (images != null) {
            SQLite.delete(ArrowImage::class.java)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (image in images!!) {
                image.arrowId = id
                image.save(databaseWrapper)
            }
        }
    }

    override fun delete() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { this.delete(it) }
    }

    override fun delete(databaseWrapper: DatabaseWrapper) {
        for (arrowImage in loadImages()!!) {
            arrowImage.delete(databaseWrapper)
        }
        super.delete(databaseWrapper)
    }

    override fun compareTo(other: Arrow) = compareBy(Arrow::name, Arrow::id).compare(this, other)

    fun areAllPropertiesSet(): Boolean {
        return !TextUtils.isEmpty(length) &&
                !TextUtils.isEmpty(material) &&
                !TextUtils.isEmpty(spine) &&
                !TextUtils.isEmpty(weight) &&
                !TextUtils.isEmpty(tipWeight) &&
                !TextUtils.isEmpty(vanes) &&
                !TextUtils.isEmpty(nock) &&
                !TextUtils.isEmpty(comment)
    }

    override fun saveRecursively() {
        save()
    }

    override fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        save(databaseWrapper)
    }

    companion object {

        val all: List<Arrow>
            get() = SQLite.select().from(Arrow::class.java).queryList()

        operator fun get(id: Long?): Arrow? {
            return if (id == null) null else SQLite.select()
                    .from(Arrow::class.java)
                    .where(Arrow_Table._id.eq(id))
                    .querySingle()
        }
    }
}
