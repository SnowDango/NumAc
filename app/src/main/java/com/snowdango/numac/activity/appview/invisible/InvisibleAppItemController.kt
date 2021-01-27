package com.snowdango.numac.activity.appview.invisible

import android.graphics.drawable.Drawable
import android.view.View
import com.airbnb.epoxy.Typed2EpoxyController
import com.snowdango.numac.*
import com.snowdango.numac.activity.appview.visible.VisibleAppItemController
import com.snowdango.numac.data.repository.dao.entity.AppInfo

class InvisibleAppItemController(
        private val appClickListener: VisibleAppItemController.AppClickListener,
        private val appLongClickListener: VisibleAppItemController.LongClickListener,
        private val verticalItemCount: Int
): Typed2EpoxyController<ArrayList<AppInfo>,Boolean>(){

    private val pm = NumApp.singletonContext().packageManager

    interface AppClickListener {
        fun appClickListener(packageName: String)
    }
    interface LongClickListener {
        fun longClickListener(appIcon: Drawable, appName: String, packageName: String, command: String, view: View): Boolean
    }

    override fun buildModels(data: ArrayList<AppInfo>?,data2: Boolean) {
        data?.forEach { appInfo ->
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

    private fun getAppIcon(packageName: String): Drawable?{
        return try{
            pm.getApplicationIcon(packageName)
        }catch (e:java.lang.Exception){
            NumApp.singletonContext().getDrawable(R.drawable.ic_android_black_108dp)!!
        }
    }

    override fun setData(data: ArrayList<AppInfo>,data2: Boolean) {
        cancelPendingModelBuild()
        if(this.isMultiSpan){ // すでにsetDataされていたら
            requestModelBuild() // Viewの更新  　
        }else {
            super.setData(data,data2) // setData
        }
    }

    companion object{
        const val TAG = "epoxy appView"
    }
}