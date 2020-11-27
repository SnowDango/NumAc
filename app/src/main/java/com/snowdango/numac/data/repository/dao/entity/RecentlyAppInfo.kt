package com.snowdango.numac.data.repository.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recently-app")
data class RecentlyAppInfo(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int?,
        @ColumnInfo(name = "package-name")
        val packageName: String

)