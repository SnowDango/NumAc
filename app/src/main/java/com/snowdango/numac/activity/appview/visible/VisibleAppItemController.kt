package com.snowdango.numac.activity.appview.visible

import android.graphics.drawable.Drawable
import android.view.View
import com.airbnb.epoxy.Typed3EpoxyController
import com.snowdango.numac.*
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

class VisibleAppItemController(
        private val appClickListener: AppClickListener,
        private val appLongClickListener: LongClickListener,
        private val verticalItemCount: Int
): Typed3EpoxyController<ArrayList<AppInfo>, ArrayList<RecentlyAppInfo>, Boolean>(){

    private val recentlyAppSize: Int = 4
    private val pm = NumApp.singletonContext().packageManager

    interface AppClickListener {
        fun appClickListener(packageName: String)
    }
    interface LongClickListener {
        fun longClickListener(appIcon:Drawable, appName: String,packageName: String,command: String,view: View): Boolean
    }

    override fun buildModels(data: ArrayList<AppInfo>,data2: ArrayList<RecentlyAppInfo>,data3:Boolean) {
        if(data3) {
            // dataが足りないときの一時data
            if(recentlyAppSize > data2.size) {
                for (num in 0 until recentlyAppSize.minus(data2.size)) {
                    data2.add(RecentlyAppInfo(id = -1, packageName = "no recently${num + 1}"))
                }
            }
            epoxyVisibleHeader {
                id("recentlyApp")
                header("recently")
                spanSizeOverride { _, _, _ -> verticalItemCount }
            }
            // recentlyのapp
            data2.forEach { appInfo ->
                epoxyVisibleItem {
                    id(appInfo.packageName)
                    if (appInfo.id != -1) {
                        val appIcon = getAppIcon(appInfo.packageName)
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
                    spanSizeOverride{_,_,_ -> verticalItemCount/4}
                }
            }
        }
        val favoriteList = data.filter { it.favorite == 1 }
        if(favoriteList.isNotEmpty()){
            epoxyVisibleHeader {
                id("favoriteApp")
                header("favorite")
                spanSizeOverride{_,_,_ -> verticalItemCount}
            }
            favoriteList.forEach {appInfo ->
                epoxyInvisibleItem {
                    id(appInfo.packageName)
                    val appIcon = getAppIcon(appInfo.packageName)
                    appIcon?.let { appIcon(it) }
                    appName(appInfo.appName)
                    appCommand(appInfo.command)
                    appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                    appIcon?.let {
                        appOnLongClickListener(View.OnLongClickListener {
                            appLongClickListener.longClickListener(appIcon, appInfo.appName, appInfo.packageName, appInfo.command, it)
                        })
                    }
                    spanSizeOverride { _, _, _ -> 2 }
                }
            }
        }
        epoxyVisibleHeader {
            id("otherApp")
            header("other")
            spanSizeOverride { _, _, _ -> verticalItemCount}
        }
        // recently以外のapp
        data.forEach { appInfo ->
            epoxyInvisibleItem {
                id(appInfo.packageName)
                val appIcon = getAppIcon(appInfo.packageName)
                appIcon?.let { appIcon(it) }
                appName(appInfo.appName)
                appCommand(appInfo.command)
                appOnClickListener(View.OnClickListener { appClickListener.appClickListener(appInfo.packageName) })
                appIcon?.let {
                    appOnLongClickListener(View.OnLongClickListener {
                        appLongClickListener.longClickListener(appIcon, appInfo.appName, appInfo.packageName, appInfo.command, it)
                    })
                }
                spanSizeOverride {_,_,_ -> 2}
            }
        }
    }

    private fun getAppIcon(packageName: String):Drawable?{
        return try{
            pm.getApplicationIcon(packageName)
        }catch (e:java.lang.Exception){
            NumApp.singletonContext().getDrawable(R.drawable.ic_android_black_108dp)!!
        }
    }

    override fun setData(data1: ArrayList<AppInfo>, data2: ArrayList<RecentlyAppInfo>,data3: Boolean) {
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
