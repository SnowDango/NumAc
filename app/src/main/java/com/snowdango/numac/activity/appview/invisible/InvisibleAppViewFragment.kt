package com.snowdango.numac.activity.appview.invisible

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.snowdango.numac.NumApp
import com.snowdango.numac.R
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionCreator
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionState
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.visible.ToggleVisibleActionCreator
import com.snowdango.numac.activity.appview.CommandChangeListener
import com.snowdango.numac.activity.appview.DataObserver
import com.snowdango.numac.activity.appview.visible.VisibleAppItemController
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo
import com.snowdango.numac.store.appview.AppViewStore
import kotlinx.android.synthetic.main.fragment_invisible_appview.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class InvisibleAppViewFragment: Fragment(), DataObserver{

    private val activityStore: AppViewStore by sharedViewModel()
    private val databaseActionCreator: AppListDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // databaseから持ってくる
    private val appInvisibleListDatabaseActionCreator: AppInvisibleListDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) }
    private val removeAppActionCreator: RemoveAppActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } //アプリ削除
    private val toggleVisibleActionCreator: ToggleVisibleActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // 非表示トグル用

    private val verticalItemCount: Int by lazy {
        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) 12 else 8
    }

    private val appItemController:  InvisibleAppItemController by lazy {
        InvisibleAppItemController(object : VisibleAppItemController.AppClickListener {
            override fun appClickListener(packageName: String) {
                val intent = NumApp.singletonContext().packageManager.getLaunchIntentForPackage(packageName)
                intent?.let { startActivity(intent) }
            }
        }, object : VisibleAppItemController.LongClickListener {
            override fun longClickListener(appIcon: Drawable, appName: String, packageName: String, command: String, view: View): Boolean {
                setPopupMenu(appIcon, appName, packageName, command, view)
                return true
            }
        }, verticalItemCount)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invisible_appview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appItemController.setFilterDuplicates(true)
        recyclerViewInVisibleApp.also { epoxyRecyclerView ->
            epoxyRecyclerView.adapter = appItemController.adapter
            epoxyRecyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, verticalItemCount).also { gridLayoutManager ->
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                gridLayoutManager.spanSizeLookup = appItemController.spanSizeLookup
            }
        }

        if(activityStore.invisibleAppActionData.value is AppInvisibleListDatabaseActionState.Success)
            appItemController.setData(
                    (activityStore.invisibleAppActionData.value as AppInvisibleListDatabaseActionState.Success).appInfo, true)
    }

    override fun onStart() {
        // databaseから持ってくる
        if (activityStore.invisibleAppActionData.value !is AppInvisibleListDatabaseActionState.Success) {
            Log.d("Load", "store don\'t have data")
            appInvisibleListDatabaseActionCreator.execute()
        }
        super.onStart()
    }

    override fun viewDataBaseChangeListener(appList:ArrayList<AppInfo>,recentlyAppList: ArrayList<RecentlyAppInfo>){}

    override fun viewInvisibleDataChangeListener(appList: ArrayList<AppInfo>){
        appItemController.setData(appList, false)
    }

    override fun removeAppListener() = appInvisibleListDatabaseActionCreator.execute()
    override fun removeAppFailedListener(packageName: String) {
        removeAppActionCreator.execute(packageName)
    }
    override fun changeCommandListener() = appInvisibleListDatabaseActionCreator.execute()
    override fun updateFavoriteListener(){}
    override fun updateVisibleListener() {
        appInvisibleListDatabaseActionCreator.execute()
        databaseActionCreator.getExecute()
    }

    override fun searchViewListener(filter: String) {
        if (activityStore.invisibleAppActionData.value is AppInvisibleListDatabaseActionState.Success) {
            val filterData =
                    (activityStore.invisibleAppActionData.value as AppInvisibleListDatabaseActionState.Success).appInfo
                            .filter { it.appName.indexOf(filter) != -1 }
            if (activityStore.invisibleAppActionData.value is AppInvisibleListDatabaseActionState.Success) {
                appItemController.setData(filterData.toCollection(ArrayList()), false)
            }
        }
    }

    override fun onTextQueryChangeEmpty() {
        if (activityStore.invisibleAppActionData.value is AppInvisibleListDatabaseActionState.Success) {
            if (activityStore.invisibleAppActionData.value is AppInvisibleListDatabaseActionState.Success) {
                appItemController.setData(
                        (activityStore.invisibleAppActionData.value as AppInvisibleListDatabaseActionState.Success).appInfo,
                        true)
            } else {
                appItemController.setData(
                        (activityStore.invisibleAppActionData.value as AppInvisibleListDatabaseActionState.Success).appInfo,
                        false)
            }
        }
    }

    private fun setPopupMenu(appIcon: Drawable, appName: String, packageName: String, command: String, view: View) {
        val popupMenu = popupMenu {
            section {
                item {
                    label = "command replace"
                    icon = R.drawable.ic_baseline_edit_24
                    callback = { (activity as CommandChangeListener).commandChangeListener(appIcon, appName, packageName, command) }
                }
                item {
                    label = "uninstall app"
                    icon = R.drawable.ic_baseline_clear_24
                    callback = { uninstallPackageWithPermissionCheck(packageName) }
                }
                item {
                    label = "visible"
                    icon = R.drawable.ic_baseline_visibility_24
                    callback = { toggleVisibleActionCreator.execute(packageName) }
                }
            }
        }
        activity?.applicationContext?.let { popupMenu.show(it, view) }
    }

    @NeedsPermission(Manifest.permission.REQUEST_DELETE_PACKAGES)
    fun uninstallPackage(packageName: String){
        val uninstallIntent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${packageName}"))
        startActivity(uninstallIntent)
    }

    @OnPermissionDenied(Manifest.permission.REQUEST_DELETE_PACKAGES)
    fun deletePackagePermissionDenied(){
        Toast.makeText(activity?.applicationContext, """Can not delete | Need Permission """.trimMargin(), Toast.LENGTH_LONG).show()
    }

}