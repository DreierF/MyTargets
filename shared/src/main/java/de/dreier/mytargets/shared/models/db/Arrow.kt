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
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Arrow(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

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
                 var thumbnail: Thumbnail? = null) : BaseModel(), IImageProvider, IIdSettable, Comparable<Arrow>, IRecursiveModel, Parcelable {

    @IgnoredOnParcel
    var images: List<ArrowImage>? = null

    val drawable: Drawable
        get() = thumbnail!!.roundDrawable

    @OneToMany(methods = [], variableName = "images")
    fun loadImages(): List<ArrowImage> {
        if (images == null) {
            images = if (id == 0L) mutableListOf() else SQLite.select()
                    .from(ArrowImage::class.java)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .queryList()
        }
        return images!!
    }

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    override fun save(): Boolean {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { this.save(it) }
        return true
    }

    override fun save(databaseWrapper: DatabaseWrapper): Boolean {
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
        return true
    }

    override fun delete(): Boolean {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { this.delete(it) }
        return true
    }

    override fun delete(databaseWrapper: DatabaseWrapper): Boolean {
        for (arrowImage in loadImages()) {
            arrowImage.delete(databaseWrapper)
        }
        super.delete(databaseWrapper)
        return true
    }

    override fun compareTo(other: Arrow) = compareBy(Arrow::name, Arrow::id).compare(this, other)

    override fun saveRecursively() {
        save()
    }

    override fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        save(databaseWrapper)
    }
}
