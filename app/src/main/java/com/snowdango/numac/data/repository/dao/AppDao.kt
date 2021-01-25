package com.snowdango.numac.data.repository.dao

import androidx.room.*
import com.snowdango.numac.data.repository.dao.entity.AppInfo


@Dao
interface AppDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(appInfo: AppInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(appInfoList: List<AppInfo>)

    @Update
    fun update(appInfo: AppInfo)

    @Delete
    fun delete(appInfo: AppInfo)

    @Query("delete from `app-info`")
    fun deleteAll()

    @Query("select * from `app-info`")
    fun getAppInfoList(): List<AppInfo>

    @Query("select * from `app-info` where visible=0")
    fun getVisibleList(): List<AppInfo>

    @Query("select * from `app-info` where visible=1")
    fun getUnVisibleList(): List<AppInfo>

    @Query("select * from `app-info` where package_name=:packageName")
    fun getAppInfoByPackageName(packageName: String): List<AppInfo>

    @Query("update `app-info` set command=:command where package_name=:packageName")
    fun updateCommandByPackageName(command:String,packageName: String)

    @Query("update `app-info` set visible=:visibleType where package_name=:packageName")
    fun updateVisibleTypePackageName(visibleType: Int, packageName: String)

    @Query("delete from `app-info` where package_name=:packageName")
    fun deleteAppByPackageName(packageName: String)

    @Query("update `app-info` set favorite=:favorite where package_name=:packageName")
    fun updateFavorite(packageName: String,favorite: Int)
}
