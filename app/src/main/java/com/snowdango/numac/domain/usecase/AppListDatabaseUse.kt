package com.snowdango.numac.domain.usecase


import com.snowdango.numac.SingletonContext
import com.snowdango.numac.data.repository.AppDataBase
import com.snowdango.numac.data.repository.dao.entity.AppInfo

class AppListDatabaseUse() {

    sealed class DatabaseResult{
        class Success(val appList: ArrayList<AppInfo>): DatabaseResult()
        object Failed: DatabaseResult()
    }

    fun appListInsert(appInfoList: ArrayList<AppInfo>){
        val dao = AppDataBase.getDatabase(SingletonContext.applicationContext()).appDao()
        dao.insertAll(appInfoList.toList())
    }

    fun getAppList(): DatabaseResult{
        return try {
            val dao = AppDataBase.getDatabase(SingletonContext.applicationContext()).appDao()
            val appList = dao.getAppInfoList().toCollection(ArrayList())
            DatabaseResult.Success(appList)
        }catch (e: Exception){
            DatabaseResult.Failed
        }
    }

}
