package com.snowdango.numac.activity.appview

import android.util.Log
import android.view.View
import com.airbnb.epoxy.Typed2EpoxyController
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.appItem
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

class AppItemController(
        private val appClickListener: AppClickListener
): Typed2EpoxyController<ArrayList<AppInfo>, ArrayList<RecentlyAppInfo>>(){

    private val recentlyQuantity: Int = 4

    interface AppClickListener {
        fun appClickListener(string: String)
    }

    override fun buildModels(data: ArrayList<AppInfo>?,data2: ArrayList<RecentlyAppInfo>) {
        Log.d(TAG,data.toString())
        val pm = SingletonContext.applicationContext().packageManager
        for (num in 0 until recentlyQuantity.minus(data2.size)) {
            data2.add(RecentlyAppInfo(id = -1, packageName = "no recently"))
        }
        Log.d(TAG,data2.toString())
        data2.forEach{ appInfo ->
            appItem {
                id(appInfo.packageName)
                if(appInfo.id != -1){
                    val appIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                    appIcon(appIntent?.let { pm.getActivityIcon(it) })
                    val recentlyApp = data?.filter { it.packageName == appInfo.packageName }
                    Log.d(TAG,recentlyApp.toString())
                    appName(recentlyApp?.get(0)?.appName)
                    appCommand(recentlyApp?.get(0)?.command)
                    appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName)})
                }else{
                    appIcon(SingletonContext.applicationContext().getDrawable(R.drawable.ic_android_black_108dp))
                    appName(appInfo.packageName)
                    appCommand("")
                }
            }
        }
        data?.forEach { appInfo ->
            val filterData = data2.filter { it.packageName == appInfo.packageName }
            if(filterData.isEmpty()) {
                appItem {
                    id(appInfo.packageName)
                    val appIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                    appIcon(appIntent?.let { pm.getActivityIcon(it) })
                    appName(appInfo.appName)
                    appCommand(appInfo.command)
                    appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                }
            }
        }
    }

    override fun setData(data1: ArrayList<AppInfo>?, data2: ArrayList<RecentlyAppInfo>?) {
        if(this.isMultiSpan){
            requestModelBuild()
        }else {
            super.setData(data1, data2)
        }
    }

    companion object{
        const val TAG = "epoxy appView"
    }
}
