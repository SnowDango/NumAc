package com.snowdango.numac.activity.appview.visible

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
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
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.visible.ToggleVisibleActionCreator
import com.snowdango.numac.activity.appview.CommandChangeListener
import com.snowdango.numac.activity.appview.DataObserver
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo
import com.snowdango.numac.store.appview.AppViewStore
import kotlinx.android.synthetic.main.fragment_visible_appview.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class VisibleAppViewFragment: Fragment(), DataObserver {

    private val activityStore: AppViewStore by sharedViewModel()
    private val databaseActionCreator: AppListDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // databaseから持ってくる
    private val appInvisibleListDatabaseActionCreator: AppInvisibleListDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) }
    private val recentlyAppDatabaseActionCreator: RecentlyAppDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // 履歴を持ってくる
    private val removeAppActionCreator: RemoveAppActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } //アプリ削除
    private val controlFavoriteActionCreator: ControlFavoriteActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // お気に入り用
    private val toggleVisibleActionCreator: ToggleVisibleActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // 非表示トグル用

    private val verticalItemCount: Int by lazy {
        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) 12 else 8
    }

    private val appItemController: VisibleAppItemController by lazy {
        VisibleAppItemController(object : VisibleAppItemController.AppClickListener {
            override fun appClickListener(packageName: String) {
                recentlyAppDatabaseActionCreator.executeUpdate(packageName)
                val intent = NumApp.singletonContext().packageManager.getLaunchIntentForPackage(packageName)
                startActivity(intent)
            }
        }, object : VisibleAppItemController.LongClickListener {
            override fun longClickListener(appIcon: Drawable, appName: String, packageName: String, command: String, view: View): Boolean {
                setPopupMenu(appIcon, appName, packageName, command, view)
                return true
            }
        }, verticalItemCount)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_visible_appview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appItemController.setFilterDuplicates(true)
        recyclerViewVisibleApp.also { epoxyRecyclerView ->
            epoxyRecyclerView.adapter = appItemController.adapter
            epoxyRecyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, verticalItemCount).also { gridLayoutManager ->
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                gridLayoutManager.spanSizeLookup = appItemController.spanSizeLookup
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        if(activityStore.databaseActionData.value is DatabaseActionState.Success)
            if(activityStore.recentlyActionData.value is RecentlyAppDatabaseActionState.Success)
                appItemController.setData(
                        (activityStore.databaseActionData.value as DatabaseActionState.Success).appList,
                        (activityStore.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList,
                        true)

        Log.d("view model error", "attach")
        super.onAttach(context)
    }

    override fun onStart() {
        // databaseから持ってくる
        if (activityStore.databaseActionData.value !is DatabaseActionState.Success) {
            Log.d("Load", "store don\'t have data")
            databaseActionCreator.getExecute()
        }
        super.onStart()
    }

    override fun viewDataBaseChangeListener(appList: ArrayList<AppInfo>,recentlyAppList: ArrayList<RecentlyAppInfo>) {
        appItemController.setData(appList,recentlyAppList,true)
    }

    override fun viewInvisibleDataChangeListener(appList: ArrayList<AppInfo>){}

    override fun removeAppListener() {
        databaseActionCreator.getExecute()
        recentlyAppDatabaseActionCreator.executeGet()
    }

    override fun removeAppFailedListener(packageName: String) {
        removeAppActionCreator.execute(packageName)
    }

    override fun changeCommandListener() {
        databaseActionCreator.getExecute()
        recentlyAppDatabaseActionCreator.executeGet()
    }

    override fun updateFavoriteListener(){
        try {
            databaseActionCreator.getExecute()
        }catch (e:Exception){
            Log.d("view model error", " not found view model")
        }
    }

    override fun updateVisibleListener() {
        databaseActionCreator.getExecute()
        appInvisibleListDatabaseActionCreator.execute()
    }

    override fun searchViewListener(filter: String) {
        if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
            val filterData =
                    (activityStore.databaseActionData.value as DatabaseActionState.Success).appList
                            .filter { it.appName.indexOf(filter) != -1 }
            if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
                appItemController.setData(
                        filterData.toCollection(ArrayList()),
                        arrayListOf(),
                        false)
            }
        }
    }

    override fun onTextQueryChangeEmpty() {
        if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
            if (activityStore.recentlyActionData.value is RecentlyAppDatabaseActionState.Success) {
                appItemController.setData(
                        (activityStore.databaseActionData.value as DatabaseActionState.Success).appList,
                        (activityStore.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList,
                        true)
            } else {
                appItemController.setData(
                        (activityStore.databaseActionData.value as DatabaseActionState.Success).appList,
                        arrayListOf(),
                        false)
            }
        }
    }

    private fun setPopupMenu(appIcon: Drawable, appName: String, packageName: String, command: String, view: View) {
        val targetApp = (activityStore.databaseActionData.value as DatabaseActionState.Success).appList.find { it.packageName == packageName }
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
                    label = "favorite"
                    icon = R.drawable.ic_baseline_star_24
                    iconColor = if (targetApp?.favorite == 0) Color.GRAY else Color.YELLOW
                    callback = {
                        if (targetApp?.favorite == 0) controlFavoriteActionCreator.execute(packageName, 1)
                        else controlFavoriteActionCreator.execute(packageName, 0)
                    }
                }
                item {
                    label = "visible"
                    icon = R.drawable.ic_baseline_visibility_off_24
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