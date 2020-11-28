package com.snowdango.numac.data.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo


@Dao
interface RecentlyDao {

    @Insert
    fun insert(recentlyAppInfo: RecentlyAppInfo)

    @Delete
    fun delete(recentlyAppInfo: RecentlyAppInfo)

    @Query("delete from `recently-app` where id =:id ")
    fun deleteAt(id: Int)

    @Query("delete from `recently-app` where `package-name`=:packageName")
    fun deleteAtPackageName(packageName: String)

    @Query("select * from `recently-app`")
    fun getAll():List<RecentlyAppInfo>

    @Query("select min(id) from `recently-app`")
    fun getMinId():Int


    fun updateRecently(recentlyAppInfo: RecentlyAppInfo): List<RecentlyAppInfo>{
        val data = getAll()
        val dataQuantity = data.size
        if(data.none { it.packageName == recentlyAppInfo.packageName }){
            if(dataQuantity >= 4){
                deleteAt(getMinId())
            }
        }else{
            deleteAtPackageName(recentlyAppInfo.packageName)
        }
        insert(recentlyAppInfo)
        return getAll()
    }

}