package com.snowdango.numac.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.snowdango.numac.data.repository.dao.AppDao
import com.snowdango.numac.data.repository.dao.RecentlyDao
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo


@Database(entities = [AppInfo::class, RecentlyAppInfo::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract fun appDao(): AppDao
    abstract fun recentlyDao(): RecentlyDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java, "num_activity"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }

        }

}

