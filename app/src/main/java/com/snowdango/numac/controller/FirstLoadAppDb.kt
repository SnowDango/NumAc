package com.snowdango.numac.controller

import android.content.Context
import com.snowdango.numac.models.DataBaseHelper
import com.snowdango.numac.models.AppListFormat
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

    fun updateList(dataBaseHelper: DataBaseHelper,context: Context?){
        val appListCreate = AppListCreate()
        val queryList = appListCreate.appRead(context!!) as ArrayList<AppListFormat>?
        for( app in queryList!!){
            if(!dataBaseHelper.getDataExist(dataBaseHelper, app.appPackageName!!)){
                dataBaseHelper.insertData(dataBaseHelper,app.appName,app.appPackageName,app.appClassName,app.appCommand)
            }
        }
        val appIcon = appListCreate.appIcon(context)
        val appPackage = appListCreate.appPackage(context)
        NumAcActivity.list?.clear()
        NumAcActivity.list = dataBaseHelper.getList(dataBaseHelper)
        val iterator = NumAcActivity.list?.iterator()
        while (iterator?.hasNext()!!){
            val app = iterator.next()
            val id = appPackage.indexOf(app.appPackageName)
            if(id != -1) {
                app.appIcon = appIcon[id]
            }else{
                dataBaseHelper.deleteApp(dataBaseHelper, app.appPackageName!!)
                iterator.remove()
            }
        }
    }
}