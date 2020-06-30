package com.snowdango.numac.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

/// create database and (read and write)

class DataBaseHelper
(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // create db
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_ENTRIES2)
        Log.d("debug", "onCreate(SQLiteDatabase db)")
    }

    // upgrade db
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // アップデートの判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        )
        onCreate(db)
        db.execSQL(
                SQL_DELETE_ENTRIES2
        )
        onCreate(db)
    }

    fun getList(dataBaseHelper: DataBaseHelper): ArrayList<AppListFormat> {
        val db = dataBaseHelper.readableDatabase
        val appList = ArrayList<AppListFormat>()
        val cursor = db.query(
                TABLE_NAME, arrayOf(COLUMN_NAME_TITLE1, COLUMN_NAME_TITLE2,
                COLUMN_NAME_TITLE3, COLUMN_NAME_TITLE4),
                null,
                null,
                null,
                null,
                null
        )
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            val appListFormat = AppListFormat()
            appListFormat.appName = cursor.getString(0)
            appListFormat.appPackageName = cursor.getString(1)
            appListFormat.appClassName = cursor.getString(2)
            appListFormat.appCommand = cursor.getString(3)
            appList.add(appListFormat)
            cursor.moveToNext()
        }
        cursor.close()
        return appList
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun getPackageAndClass(dataBaseHelper: DataBaseHelper, command: String): Array<String> {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery("select $COLUMN_NAME_TITLE2 , $COLUMN_NAME_TITLE3 from $TABLE_NAME where $COLUMN_NAME_TITLE4 = ?", arrayOf(command))
        cursor.moveToFirst()
        return arrayOf(cursor.getString(0), cursor.getString(1))
    }

    fun getAppNameList(dataBaseHelper: DataBaseHelper): ArrayList<String> {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery("select $COLUMN_NAME_TITLE1 from $TABLE_NAME", arrayOf())
        cursor.moveToFirst()
        val appDataList = ArrayList<String>()
        for (i in 0 until cursor.count) {
            appDataList.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return appDataList
    }

    fun getAppCommandList(dataBaseHelper: DataBaseHelper): ArrayList<String> {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery("select $COLUMN_NAME_TITLE4 from $TABLE_NAME", arrayOf())
        cursor.moveToFirst()
        val appDataList = ArrayList<String>()
        for (i in 0 until cursor.count) {
            appDataList.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return appDataList
    }

    fun insertData(dataBaseHelper: DataBaseHelper, appName: String?, packageName: String?,
                   className: String?, num: String?) {
        val db = dataBaseHelper.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME_TITLE1, appName)
        values.put(COLUMN_NAME_TITLE2, packageName)
        values.put(COLUMN_NAME_TITLE3, className)
        values.put(COLUMN_NAME_TITLE4, num)
        db.insert(TABLE_NAME, null, values)
    }

    fun insertColor(dataBaseHelper: DataBaseHelper, color: String?) {
        val db = dataBaseHelper.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME_TITLE5, color)
        db.insert(TABLE_NAME2, null, values)
    }

    fun updateCommandWhereName(dataBaseHelper: DataBaseHelper, appName: String, newCommand: String) {
        val db = dataBaseHelper.writableDatabase
        db.execSQL("update $TABLE_NAME set $COLUMN_NAME_TITLE4 = ? where $COLUMN_NAME_TITLE1 = ?", arrayOf(newCommand, appName))
    }

    fun updateColor(dataBaseHelper: DataBaseHelper, color: String) {
        val db = dataBaseHelper.writableDatabase
        db.execSQL("update $TABLE_NAME2 set $COLUMN_NAME_TITLE5 = ? where $_ID = 1", arrayOf(color))
    }

    fun getThemeColor(dataBaseHelper: DataBaseHelper): String {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery("select $COLUMN_NAME_TITLE5 from $TABLE_NAME2", arrayOf())
        cursor.moveToFirst()
        Log.d("theme", cursor.getString(0))
        return cursor.getString(0)
    }

    fun clearTable(dataBaseHelper: DataBaseHelper) {
        val db = dataBaseHelper.writableDatabase
        db.rawQuery("delete from $TABLE_NAME", arrayOf())
    }

    fun deleteApp(dataBaseHelper: DataBaseHelper, appName: String) {
        val db = dataBaseHelper.writableDatabase
        db.execSQL("delete from $TABLE_NAME where $COLUMN_NAME_TITLE1 = ?", arrayOf(appName))
    }

    companion object {
        //database version
        private const val DATABASE_VERSION = 1

        // database name
        private const val DATABASE_NAME = "AppList.db"
        private const val TABLE_NAME = "app_list"
        private const val TABLE_NAME2 = "theme_base"

        // column name for table1
        private const val _ID = "_id"
        private const val COLUMN_NAME_TITLE1 = "app_name"
        private const val COLUMN_NAME_TITLE2 = "package_name"
        private const val COLUMN_NAME_TITLE3 = "class_name"
        private const val COLUMN_NAME_TITLE4 = "command"

        //column name for table2
        private const val COLUMN_NAME_TITLE5 = "theme_color"

        // entries of create sql table1
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TITLE1 + " TEXT," +
                COLUMN_NAME_TITLE2 + " TEXT," +
                COLUMN_NAME_TITLE3 + " TEXT," +
                COLUMN_NAME_TITLE4 + " TEXT)"

        //entries of create sql table2
        private const val SQL_CREATE_ENTRIES2 = "CREATE TABLE " + TABLE_NAME2 + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TITLE5 + " TEXT) "

        //entries of delete sql
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
        private const val SQL_DELETE_ENTRIES2 = "DROP TABLE IF EXISTS $TABLE_NAME2"
    }
}