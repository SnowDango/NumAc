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

}
