package com.snowdango.numac.domain.usecase

import com.snowdango.numac.NumApp
import com.snowdango.numac.data.repository.AppDataBase
import com.snowdango.numac.data.repository.dao.entity.AppInfo

class AppListDatabaseUse() {

    sealed class DatabaseResult{
        class Success(val appList: ArrayList<AppInfo>): DatabaseResult()
        object Failed: DatabaseResult()
    }

    fun appListInsert(appInfoList: ArrayList<AppInfo>){
        val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
        dao.insertAll(appInfoList.toList())
    }

    fun getAppList(): DatabaseResult{
        return try {
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            val appList = dao.getAppInfoList().toCollection(ArrayList())
            DatabaseResult.Success(appList)
        }catch (e: Exception){
            DatabaseResult.Failed
        }
    }

    fun getAppVisibleList(): DatabaseResult{
        return try {
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            val visibleList = dao.getVisibleList().toCollection(ArrayList())
            DatabaseResult.Success(visibleList)
        }catch (e: Exception){
            DatabaseResult.Failed
        }
    }

    fun getAppInVisibleList(): DatabaseResult{
        return try {
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            val visibleList = dao.getUnVisibleList().toCollection(ArrayList())
            DatabaseResult.Success(visibleList)
        }catch (e: Exception){
            DatabaseResult.Failed
        }
    }

    fun removeApp(packageName: String): DatabaseResult{
        return try{
            val appDao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            appDao.deleteAppByPackageName(packageName)
            val recentDao = AppDataBase.getDatabase(NumApp.singletonContext()).recentlyDao()
            recentDao.removeRecentlyAppByPackageName(packageName)
            DatabaseResult.Success(arrayListOf())
        }catch (e: Exception){
            DatabaseResult.Failed
        }
    }

    fun updateCommand(packageName: String,command: String): DatabaseResult {
        return try{
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            dao.updateCommandByPackageName(command,packageName)
            DatabaseResult.Success(arrayListOf())
        }catch (e:Exception){
            DatabaseResult.Failed
        }
    }

    fun updateFavorite(packageName: String,favorite: Int): DatabaseResult {
        return try{
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            dao.updateFavorite(packageName,favorite)
            DatabaseResult.Success(arrayListOf())
        }catch (e:Exception){
            DatabaseResult.Failed
        }
    }

    fun updateVisible(packageName: String): DatabaseResult{
        return try{
            val dao = AppDataBase.getDatabase(NumApp.singletonContext()).appDao()
            val visibleType: List<AppInfo> = dao.getAppInfoByPackageName(packageName)
            val updateVisibleType = if(visibleType[0].visible == 0) 1 else 0
            dao.updateVisibleTypePackageName(updateVisibleType, packageName)
            val recentlyDao = AppDataBase.getDatabase(NumApp.singletonContext()).recentlyDao()
            val recentlyList = recentlyDao.getAll()
            if(recentlyList.find { it.packageName == packageName } != null){
                recentlyDao.deleteAtPackageName(packageName)
            }
            DatabaseResult.Success(arrayListOf())
        }catch (e:Exception){
            DatabaseResult.Failed
        }
    }
}
