package com.snowdango.numac.activity.appview

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.snowdango.numac.R
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.store.appview.AppViewStore
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.android.synthetic.main.activity_appview.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AppViewActivity: AppCompatActivity() {

    private val coroutineScope: CancellableCoroutineScope = CancellableCoroutineScope()
    private val databaseActionCreate: AppListDatabaseActionCreator by inject { parametersOf(coroutineScope) }
    private val recentlyAppDatabaseActionCreator: RecentlyAppDatabaseActionCreator by inject { parametersOf(coroutineScope) }
    private val store: AppViewStore by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)

        val appItemController = AppItemController(object : AppItemController.AppClickListener {
            override fun appClickListener(packageName: String) {
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.let {
                    recentlyAppDatabaseActionCreator.execute(0, packageName)
                    startActivity(intent)
                }
            }
        }, object : AppItemController.LongClickListener {
            override fun longClickListener(appIcon: Drawable,appName: String,packageName: String,command:String,view: View): Boolean {
                setPopupMenu(appIcon,appName, packageName, command, view)
                return true
            }
        })
        recyclerViewApp.apply {
            adapter = appItemController.adapter
            layoutManager = GridLayoutManager(applicationContext, 4).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }
        // search View のCallBack
        searchCallback(appItemController)
        // observerの設定
        observeValue(appItemController)
    }

    override fun onStart() {
        databaseActionCreate.getExecute() // databaseから持ってくる
        super.onStart()
    }

    private fun observeValue(appItemController: AppItemController) {
        val databaseObserve = Observer<DatabaseActionState> {
            when (it) {
                is DatabaseActionState.None -> return@Observer
                is DatabaseActionState.Failed -> Toast.makeText(this, "miss database", Toast.LENGTH_SHORT).show()
                is DatabaseActionState.Success ->
                    if (store.recentlyActionData.value is RecentlyAppDatabaseActionState.Success) {
                        progressMaterial.visibility = View.GONE
                        appItemController.setData(it.appList, (store.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList, true)
                    } else {
                        recentlyAppDatabaseActionCreator.execute(1, "")
                    }
            }
        }
        val recentlyObserve = Observer<RecentlyAppDatabaseActionState> {
            when (it) {
                is RecentlyAppDatabaseActionState.None -> return@Observer
                is RecentlyAppDatabaseActionState.Failed -> Toast.makeText(this, "miss database", Toast.LENGTH_SHORT).show()
                is RecentlyAppDatabaseActionState.Success ->
                    if (store.databaseActionData.value is DatabaseActionState.Success) {
                        progressMaterial.visibility = View.GONE
                        appItemController.setData((store.databaseActionData.value as DatabaseActionState.Success).appList, it.recentlyList, true)
                    }
            }
        }

        store.databaseActionData.observe(this, databaseObserve)
        store.recentlyActionData.observe(this, recentlyObserve)
    }

    private fun searchCallback(appItemController: AppItemController) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                progressMaterial.visibility = View.VISIBLE
                p0?.let {
                    if (it != StringBuilder().toString()) {
                        searchFilter(p0, appItemController)
                    } else {
                        onTextQueryChangeEmpty(appItemController)
                    }
                } ?: onTextQueryChangeEmpty(appItemController)
                return true
            }
        })
    }

    private fun searchFilter(filterText: String, appItemController: AppItemController) {
        if (store.databaseActionData.value is DatabaseActionState.Success) {
            val filterData =
                    (store.databaseActionData.value as DatabaseActionState.Success).appList
                            .filter { it.appName.indexOf(filterText) != -1 }
            if (store.databaseActionData.value is DatabaseActionState.Success) {
                appItemController.setData(
                        filterData.toCollection(ArrayList()),
                        arrayListOf(),
                        false
                )
                progressMaterial.visibility = View.GONE
            }
        }
    }

    private fun onTextQueryChangeEmpty(appItemController: AppItemController) {
        if (store.databaseActionData.value is DatabaseActionState.Success) {
            if (store.recentlyActionData.value is RecentlyAppDatabaseActionState.Success) {
                appItemController.setData(
                        (store.databaseActionData.value as DatabaseActionState.Success).appList,
                        (store.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList,
                        true)
            } else {
                appItemController.setData(
                        (store.databaseActionData.value as DatabaseActionState.Success).appList,
                        arrayListOf(),
                        false)
            }
            progressMaterial.visibility = View.GONE
        }
    }

    private fun setPopupMenu(appIcon: Drawable,appName:String,packageName: String,command: String,view: View) {
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
                }
            }
        }
        popupMenu.show(this,view)
    }

    private fun commandReplaceDialog(appIcon: Drawable,appName: String,packageName: String, command: String){
        MaterialDialog(this).show {
            title(text = appName)
            icon(drawable = appIcon)
            message(text = "old command = $command") {
                messageTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            }
            input(hint = "new command",inputType = InputType.TYPE_CLASS_NUMBER,maxLength = 4)
            positiveButton(text = "Enter"){

            }
        }
    }
}
