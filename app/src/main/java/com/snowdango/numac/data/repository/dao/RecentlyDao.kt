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

    @Query("select * from `recently-app`")
    fun getAll():List<RecentlyAppInfo>

    @Query("select min(id) from `recently-app`")
    fun getMinId():Int


    fun updateRecently(recentlyAppInfo: RecentlyAppInfo): List<RecentlyAppInfo>{
        val dataQuantity = getAll().size
        if(dataQuantity >= 4){
            deleteAt(getMinId())
        }
        insert(recentlyAppInfo)
        return getAll()
    }

}