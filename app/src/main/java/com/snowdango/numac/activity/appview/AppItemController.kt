package com.snowdango.numac.activity.appview

import android.content.pm.PackageManager
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.appItem
import com.snowdango.numac.data.repository.dao.entity.AppInfo

class AppItemController(
        private val appClickListener: AppClickListener
): TypedEpoxyController<ArrayList<AppInfo>>(){

    interface AppClickListener {
        fun appClickListener(string: String)
    }

    override fun buildModels(data: ArrayList<AppInfo>?) {
        val pm = SingletonContext.applicationContext().packageManager
        data?.forEach { appInfo ->
            appItem {
                id("appView")
                val appIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                appIcon(appIntent?.let { pm.getActivityIcon(it) })
                appName(appInfo.appName)
                appCommand(appInfo.command)
                appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
            }
        }
    }
}
