package com.snowdango.numac.controller

import android.content.Context
import com.snowdango.numac.model.DataBaseHelper
import com.snowdango.numac.model.AppListFormat
import com.snowdango.numac.activites.NumAcActivity
import java.util.*

/*
this class can organize app list .
First launch : listen app list create and set view mode "daylight".
over Second lunch : listen app list create.
If command exist this app , replace new list in command.
 */

class FirstLoadAppDb {
    fun firstCreateDb(dataBaseHelper: DataBaseHelper, context: Context?) {
        val appListCreate = AppListCreate()
        NumAcActivity.list = ArrayList()
        NumAcActivity.list = appListCreate.appRead(context!!) as ArrayList<AppListFormat>?
        for (app in NumAcActivity.list!!) {
            dataBaseHelper.insertData(dataBaseHelper, app.appName,
                    app.appPackageName, app.appClassName, app.appCommand)
        }
        dataBaseHelper.insertColor(dataBaseHelper, "daylight")
    }

    fun updateDbList(dataBaseHelper: DataBaseHelper, context: Context?) {
        val appListCreate = AppListCreate()
        NumAcActivity.list = appListCreate.appRead(context!!) as ArrayList<AppListFormat>?
        val appNameList = dataBaseHelper.getAppNameList(dataBaseHelper)
        val appCommandList = dataBaseHelper.getAppCommandList(dataBaseHelper)
        val queryList = ArrayList<AppListFormat>()
        dataBaseHelper.clearTable(dataBaseHelper)
        for (i in NumAcActivity.list!!.indices) {
            if (NumAcActivity.list!![i].appName == appNameList[i]) {
                var appListFormat = AppListFormat()
                appListFormat = NumAcActivity.list!![i]
                appListFormat.appCommand = appCommandList[i]
                queryList.add(appListFormat)
            } else {
                queryList.add(NumAcActivity.list!![i])
            }
            dataBaseHelper.insertData(dataBaseHelper, queryList[i].appName, queryList[i].appPackageName,
                    queryList[i].appClassName, queryList[i].appCommand)
        }
        NumAcActivity.list!!.clear()
        NumAcActivity.list = queryList
    }

    fun checkAppList(dataBaseHelper: DataBaseHelper, context: Context?) {
        val appListCreate = AppListCreate()
        NumAcActivity.list = ArrayList()
        NumAcActivity.list = appListCreate.appRead(context!!) as ArrayList<AppListFormat>?
        val appNameList = dataBaseHelper.getAppNameList(dataBaseHelper)
        for (i in NumAcActivity.list!!.indices) {
            if (!appNameList.contains(NumAcActivity.list!![i].appName)) {
                dataBaseHelper.insertData(dataBaseHelper, NumAcActivity.list!![i].appName,
                        NumAcActivity.list!![i].appPackageName, NumAcActivity.list!![i].appClassName, NumAcActivity.list!![i].appCommand)
            }
        }
    }

    fun setCommandList(dataBaseHelper: DataBaseHelper) {
        val appCommandList = dataBaseHelper.getAppCommandList(dataBaseHelper)
        val queryList = ArrayList<AppListFormat>()
        for (i in NumAcActivity.list!!.indices) {
            var appListFormat = AppListFormat()
            appListFormat = NumAcActivity.list!![i]
            appListFormat.appCommand = appCommandList[i]
            queryList.add(appListFormat)
        }
        NumAcActivity.list!!.clear()
        NumAcActivity.list = queryList
    }
}