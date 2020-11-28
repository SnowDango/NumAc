package com.snowdango.numac.activity.appview

import android.view.View
import com.airbnb.epoxy.Typed3EpoxyController
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.appItem
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

class AppItemController(
        private val appClickListener: AppClickListener
): Typed3EpoxyController<ArrayList<AppInfo>, ArrayList<RecentlyAppInfo>, Boolean>(){

    private val recentlyQuantity: Int = 4

    interface AppClickListener {
        fun appClickListener(string: String)
    }

    override fun buildModels(data: ArrayList<AppInfo>?,data2: ArrayList<RecentlyAppInfo>,data3:Boolean) {
        val pm = SingletonContext.applicationContext().packageManager

        if(data3) {
            // dataが足りないときの一時data
            for (num in 0 until recentlyQuantity.minus(data2.size)) {
                data2.add(RecentlyAppInfo(id = -1, packageName = "no recently"))
            }

            // recentlyのapp
            data2.forEach { appInfo ->
                appItem {
                    id(appInfo.packageName)
                    if (appInfo.id != -1) {
                        val appIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                        appIcon(appIntent?.let { pm.getActivityIcon(it) })
                        val recentlyApp = data?.filter { it.packageName == appInfo.packageName }
                        appName(recentlyApp?.get(0)?.appName)
                        appCommand(recentlyApp?.get(0)?.command)
                        appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                    } else {
                        appIcon(SingletonContext.applicationContext().getDrawable(R.drawable.ic_android_black_108dp))
                        appName(appInfo.packageName)
                        appCommand(StringBuilder().toString())
                    }
                }
            }

        }
        // recently以外のapp
        data?.forEach { appInfo ->
            val filterData = if(data3){
                data2.filter { it.packageName == appInfo.packageName }
            }else{
                arrayListOf()
            }
            if(filterData.isEmpty()) {
                appItem {
                    val appIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                    id(appInfo.packageName)
                    appIcon(appIntent?.let { pm.getActivityIcon(it) })
                    appName(appInfo.appName)
                    appCommand(appInfo.command)
                    appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                }
            }
        }
    }


    override fun setData(data1: ArrayList<AppInfo>?, data2: ArrayList<RecentlyAppInfo>?,data3: Boolean) {
        if(this.isMultiSpan){ // すでにsetDataされていたら
            requestModelBuild() // Viewの更新  　
        }else {
            super.setData(data1, data2,data3) // setData
        }
    }

    companion object{
        const val TAG = "epoxy appView"
    }
}
