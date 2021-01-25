package com.snowdango.numac.activity.appview.visible

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.snowdango.numac.R
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionCreator
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionState
import com.snowdango.numac.actions.visible.ToggleVisibleActionCreator
import com.snowdango.numac.activity.appview.DataObserver
import com.snowdango.numac.store.appview.AppViewStore
import kotlinx.android.synthetic.main.fragment_appview.*
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
    private val recentlyAppDatabaseActionCreator: RecentlyAppDatabaseActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // 履歴を持ってくる
    private val removeAppActionCreator: RemoveAppActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } //アプリ削除
    private val changeCommandActionCreator: ChangeCommandActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // コマンドチェンジ用
    private val controlFavoriteActionCreator: ControlFavoriteActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // お気に入り用
    private val toggleVisibleActionCreator: ToggleVisibleActionCreator by inject { parametersOf(activityStore.viewModelCoroutineScope) } // 非表示トグル用

    private lateinit var appItemController: VisibleAppItemController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appview,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val verticalItemCount: Int =
                if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) 12
                else 8

        appItemController = VisibleAppItemController(object : VisibleAppItemController.AppClickListener {
            override fun appClickListener(packageName: String) {
                val intent = activity?.packageManager?.getLaunchIntentForPackage(packageName)
                intent?.let { startActivity(intent) }
            }
        }, object : VisibleAppItemController.LongClickListener {
            override fun longClickListener(appIcon: Drawable, appName: String, packageName: String, command: String, view: View): Boolean {
                setPopupMenu(appIcon, appName, packageName, command, view)
                return true
            }
        }, verticalItemCount)

        appItemController.setFilterDuplicates(true)
        recyclerViewVisibleApp.also { epoxyRecyclerView ->
            epoxyRecyclerView.adapter = appItemController.adapter
            epoxyRecyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, verticalItemCount).also {gridLayoutManager ->
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                gridLayoutManager.spanSizeLookup = appItemController.spanSizeLookup
            }
        }

        if(activityStore.databaseActionData.value is DatabaseActionState.Success)
            if(activityStore.recentlyActionData.value is RecentlyAppDatabaseActionState.Success)
                appItemController.setData(
                        (activityStore.databaseActionData.value as DatabaseActionState.Success).appList,
                        (activityStore.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList,
                        true)
    }

    override fun onStart() {
        // databaseから持ってくる
        if (activityStore.databaseActionData.value !is DatabaseActionState.Success) {
            Log.d("Load","store don\'t have data")
            databaseActionCreator.getExecute()
        }
        super.onStart()
    }

    override fun viewDataBaseChangeListener(databaseActionState: DatabaseActionState.Success) {
        if (activityStore.recentlyActionData.value is RecentlyAppDatabaseActionState.Success) {
            appItemController.setData(databaseActionState.appList, (activityStore.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList, true)
        }else {
            recentlyAppDatabaseActionCreator.executeGet()
        }
    }

    override fun recentlyAppDataBaseListener(recentlyAppDatabaseActionState: RecentlyAppDatabaseActionState.Success) {
        if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
            appItemController.setData((activityStore.databaseActionData.value as DatabaseActionState.Success).appList, recentlyAppDatabaseActionState.recentlyList, true)
        }
    }

    override fun removeAppListener() {
        databaseActionCreator.getExecute()
        recentlyAppDatabaseActionCreator.executeGet()
    }

    override fun removeAppFailedListener(removeAppActionState: RemoveAppActionState.Failed) {
        removeAppActionCreator.execute(removeAppActionState.packageName)
    }

    override fun changeCommandListener() {
        databaseActionCreator.getExecute()
        recentlyAppDatabaseActionCreator.executeGet()
    }

    override fun changeFavoriteListener() = databaseActionCreator.getExecute()

    override fun changeVisibleListener() = databaseActionCreator.getExecute()

    override fun searchViewListener(filter: String) {
        if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
            val filterData =
                    (activityStore.databaseActionData.value as DatabaseActionState.Success).appList
                            .filter { it.appName.indexOf(filter) != -1 }
            if (activityStore.databaseActionData.value is DatabaseActionState.Success) {
                appItemController.setData(
                        filterData.toCollection(ArrayList()),
                        arrayListOf(),
                        false
                )
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

    private fun setPopupMenu(appIcon: Drawable, appName:String, packageName: String, command: String, view: View) {
        val targetApp = (activityStore.databaseActionData.value as DatabaseActionState.Success).appList.find { it.packageName == packageName }
        val popupMenu = popupMenu {
            section {
                item {
                    label = "command replace"
                    icon = R.drawable.ic_baseline_edit_24
                    callback = { commandReplaceDialog(appIcon, appName, packageName, command) }
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
                        if (targetApp?.favorite == 0) controlFavoriteActionCreator.execute(packageName,1)
                        else controlFavoriteActionCreator.execute(packageName,0)
                    }
                }
                item {
                    label = "visible"
                    icon = R.drawable.ic_baseline_visibility_24
                    callback = { toggleVisibleActionCreator.execute(packageName) }
                }
            }
        }
        activity?.applicationContext?.let { popupMenu.show(it,view) }
    }

    @NeedsPermission(Manifest.permission.REQUEST_DELETE_PACKAGES)
    fun uninstallPackage(packageName: String){
        val uninstallIntent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${packageName}"))
        startActivity(uninstallIntent)
    }

    @OnPermissionDenied(Manifest.permission.REQUEST_DELETE_PACKAGES)
    fun deletePackagePermissionDenied(){
        Toast.makeText(activity?.applicationContext,"""Can not delete | Need Permission """.trimMargin(),Toast.LENGTH_LONG).show()
    }

    private fun commandReplaceDialog(appIcon: Drawable,appName: String,packageName: String, command: String){
        activity?.applicationContext?.let {
            MaterialDialog(it).show {
            title(text = appName)
            icon(drawable = appIcon)
            message(text = "old command = $command") {
                messageTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            }
            input(hint = "new command",inputType = InputType.TYPE_CLASS_NUMBER,maxLength = 4
                    ,waitForPositiveButton = false,allowEmpty = false){ dialog,text ->
                val inputField = dialog.getInputField()
                val isSafeLength = text.length == 4
                inputField.error = if(isSafeLength) null else "You have to choose a 4-digit command"
                setActionButtonEnabled(WhichButton.POSITIVE,isSafeLength)
            }
            positiveButton {
                changeCommandActionCreator.execute(packageName,getInputField().text.toString())
            }
        }}
    }
}