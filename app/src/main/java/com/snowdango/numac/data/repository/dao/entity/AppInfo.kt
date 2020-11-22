package com.snowdango.numac.data.repository.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app-info")
data class AppInfo(

        @ColumnInfo(name = "app_name")
        val appName: String,
        @PrimaryKey
        @ColumnInfo(name = "package_name")
        val packageName: String,
        @ColumnInfo(name = "command")
        var command: String
)
