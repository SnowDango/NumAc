package com.snowdango.numac.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.snowdango.numac.data.repository.dao.AppDao
import com.snowdango.numac.data.repository.dao.RecentlyDao
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo


@Database(entities = [AppInfo::class, RecentlyAppInfo::class], version = 2, exportSchema = false)
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
                    ).addMigrations(migration1_2).build()
                }
            }
            return INSTANCE!!
        }
        private val migration1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `app-info` add column 'favorite' INTEGER  not null default 0 ")
            }
        }
    }
}

