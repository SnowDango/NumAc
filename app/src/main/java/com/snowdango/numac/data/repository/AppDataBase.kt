package com.snowdango.numac.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.snowdango.numac.data.repository.dao.AppDao
import com.snowdango.numac.data.repository.dao.entity.AppInfo


@Database(entities = [AppInfo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "num_activity"
                    ).build()
                }
            }
            return INSTANCE!!
        }

    }

}
