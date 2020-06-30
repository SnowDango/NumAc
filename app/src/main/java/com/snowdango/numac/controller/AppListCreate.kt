package com.snowdango.numac.controller

import android.content.Context
import android.content.Intent
import com.snowdango.numac.models.AppListFormat
import java.util.*

/*
When First Lunch this application and update app list, listened this class.
return ArrayList -> format : AppListFormat.
Launchable application set ArrayList
 Icon, Name, packageName, className and
 command( when first launch, put random four number)
 */

class AppListCreate {
    fun appRead(context: Context): List<AppListFormat> {
        val data: MutableList<AppListFormat> = ArrayList()
        val pm = context.applicationContext.packageManager
        val pkgInfoList = pm.getInstalledPackages(0)
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        for (pckInfo in pkgInfoList) {
            if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                val appInfo = AppListFormat()
                appInfo.appIcon = pckInfo.applicationInfo.loadIcon(pm)
                appInfo.appName = pckInfo.applicationInfo.loadLabel(pm) as String
                appInfo.appPackageName = pckInfo.packageName
                appInfo.appClassName = pm.getLaunchIntentForPackage(pckInfo.packageName)?.component?.className + ""
                appInfo.appCommand = randomCommand()
                data.add(appInfo)
            }
        }
        return data
    }

    private fun randomCommand(): String {
        val r = Random()
        var num = 0
        var appCommand = ""
        for (i in 0..3) {
            num = r.nextInt(10)
            appCommand += num
        }
        if (appCommand == "0000") {
            appCommand = randomCommand()
        }
        return appCommand
    }
}