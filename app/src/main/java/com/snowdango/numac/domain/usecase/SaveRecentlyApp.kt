package com.snowdango.numac.domain.usecase

import com.snowdango.numac.SingletonContext
import com.snowdango.numac.data.repository.AppDataBase
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

class SaveRecentlyApp {


    sealed class SaveRecentlyAppResult{
        data class Success(val recentlyAppList: ArrayList<RecentlyAppInfo>): SaveRecentlyAppResult()
        object Failed: SaveRecentlyAppResult()
    }


    suspend fun updateRecentlyAppList(packageName: String): SaveRecentlyAppResult{
        return try {
            val dao = AppDataBase.getDatabase(SingletonContext.applicationContext()).recentlyDao()
            val recentlyList: ArrayList<RecentlyAppInfo> = dao.updateRecently(RecentlyAppInfo(null, packageName)).toCollection(ArrayList())
            SaveRecentlyAppResult.Success(recentlyList)
        }catch (e: Exception){
            SaveRecentlyAppResult.Failed
        }

    }

    suspend fun getRecentlyAppList(): SaveRecentlyAppResult{
        return try {
            val dao = AppDataBase.getDatabase(SingletonContext.applicationContext()).recentlyDao()
            val recentlyList: ArrayList<RecentlyAppInfo> = dao.getAll().toCollection(ArrayList<RecentlyAppInfo>())
            SaveRecentlyAppResult.Success(recentlyList)
        }catch (e: Exception){
            SaveRecentlyAppResult.Failed
        }
    }
}