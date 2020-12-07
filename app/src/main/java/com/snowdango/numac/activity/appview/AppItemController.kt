package com.snowdango.numac.activity.appview

import android.graphics.drawable.Drawable
import android.view.View
import com.airbnb.epoxy.Typed3EpoxyController
import com.snowdango.numac.R
import com.snowdango.numac.NumApp
import com.snowdango.numac.appItem
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

class AppItemController(
        private val appClickListener: AppClickListener,
        private val appLongClickListener: LongClickListener
): Typed3EpoxyController<ArrayList<AppInfo>, ArrayList<RecentlyAppInfo>, Boolean>(){

    private val recentlyQuantity: Int = 4

    interface AppClickListener {
        fun appClickListener(packageName: String)
    }
    interface LongClickListener {
        fun longClickListener(appIcon:Drawable, appName: String,packageName: String,command: String,view: View): Boolean
    }

    override fun buildModels(data: ArrayList<AppInfo>?,data2: ArrayList<RecentlyAppInfo>,data3:Boolean) {
        val pm = NumApp.singletonContext().packageManager
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
                        val appIcon = try{ pm.getApplicationIcon(appInfo.packageName) }catch (e: Exception){ NumApp.singletonContext().getDrawable(R.drawable.ic_android_black_108dp) }
                        appIcon(appIcon)
                        val recentlyApp = data?.find { it.packageName == appInfo.packageName }
                        appName(recentlyApp?.appName)
                        appCommand(recentlyApp?.command)
                        appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                        appOnLongClickListener(View.OnLongClickListener {
                            recentlyApp?.let { app -> appLongClickListener.longClickListener(appIcon!!, app.appName, appInfo.packageName, app.command, it) }!!
                        })
                    } else {
                        appIcon(NumApp.singletonContext().getDrawable(R.drawable.ic_android_black_108dp))
                        appName(appInfo.packageName)
                        appCommand(StringBuilder().toString())
                    }
                }
            }
        }
        // recently以外のapp
        data?.forEach { appInfo ->
            val filterData =
                    if (data3) data2.filter { it.packageName == appInfo.packageName }
                    else arrayListOf()
            if (filterData.isEmpty()) {
                appItem {
                    id(appInfo.packageName)
                    val appIcon = try{ pm.getApplicationIcon(appInfo.packageName) }catch (e: Exception){ null }
                    appIcon?.let { appIcon(it) }
                    appName(appInfo.appName)
                    appCommand(appInfo.command)
                    appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                    appIcon?.let {
                        appOnLongClickListener(View.OnLongClickListener {
                            appLongClickListener.longClickListener(appIcon, appInfo.appName, appInfo.packageName, appInfo.command, it)
                        })
                    }
                }
            }
        }
    }

    override fun setData(data1: ArrayList<AppInfo>?, data2: ArrayList<RecentlyAppInfo>?,data3: Boolean) {
        cancelPendingModelBuild()
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
