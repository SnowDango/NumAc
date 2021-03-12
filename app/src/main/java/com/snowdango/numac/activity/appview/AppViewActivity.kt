package com.snowdango.numac.activity.appview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.ncapdevi.fragnav.FragNavController
import com.snowdango.numac.R
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionState
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionCreator
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionState
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionState
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionState
import com.snowdango.numac.actions.visible.ToggleVisibleActionState
import com.snowdango.numac.activity.appview.invisible.InvisibleAppViewFragment
import com.snowdango.numac.activity.appview.visible.VisibleAppViewFragment
import com.snowdango.numac.store.appview.AppViewStore
import kotlinx.android.synthetic.main.activity_appview.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AppViewActivity: AppCompatActivity(), View.OnClickListener, CommandChangeListener {

    private val store: AppViewStore by viewModel()
    private val visibleAppViewFragment: VisibleAppViewFragment by inject()
    private val invisibleAppViewFragment: InvisibleAppViewFragment by inject()
    private val databaseActionCreator: AppListDatabaseActionCreator by inject { parametersOf(store.viewModelCoroutineScope) } // databaseから持ってくる
    private val recentlyAppDatabaseActionCreator: RecentlyAppDatabaseActionCreator by inject { parametersOf(store.viewModelCoroutineScope) } // 履歴を持ってくる
    private val changeCommandActionCreator: ChangeCommandActionCreator by inject { parametersOf(store.viewModelCoroutineScope) } // コマンドチェンジ用
    private val removeAppActionCreator: RemoveAppActionCreator by inject { parametersOf(store.viewModelCoroutineScope) } //アプリ削除
    private val fragNavController: FragNavController by inject{ parametersOf(supportFragmentManager, R.id.frameView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)
        toggleVisible.also {
            it.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_36))
            it.setOnClickListener(this)
        }

        fragNavController.also {
            it.rootFragments = listOf(
                    visibleAppViewFragment,
                    invisibleAppViewFragment
            )
            it.initialize(FragNavController.TAB1,savedInstanceState)
        }
    }

    private fun onSetSearchTextObserver(){
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText != StringBuilder().toString()) {
                        (fragNavController.currentFrag as DataObserver).searchViewListener(newText.toString())
                    } else {
                        (fragNavController.currentFrag as DataObserver).onTextQueryChangeEmpty()
                    }
                } ?: (fragNavController.currentFrag as DataObserver).onTextQueryChangeEmpty()
                return true
            }
        })
    }

    // Observer
    private val databaseActionObserver = Observer<DatabaseActionState> { actionState ->
        when (actionState) {
            is DatabaseActionState.None -> return@Observer
            is DatabaseActionState.Failed -> Toast.makeText(applicationContext, "miss database", Toast.LENGTH_SHORT).show()
            is DatabaseActionState.Success -> fragNavController.rootFragments?.let {
                if(store.recentlyActionData.value is RecentlyAppDatabaseActionState.Success) {
                    (it[0] as DataObserver).viewDataBaseChangeListener(actionState.appList, (store.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList)
                }else{
                    recentlyAppDatabaseActionCreator.executeGet()
                }
            }
        }
    }
    private val invisibleAppActionObserver = Observer<AppInvisibleListDatabaseActionState> { actionState ->
        when(actionState){
            is AppInvisibleListDatabaseActionState.None -> return@Observer
            is AppInvisibleListDatabaseActionState.Failed -> Toast.makeText(applicationContext, "miss database", Toast.LENGTH_LONG).show()
            is AppInvisibleListDatabaseActionState.Success -> fragNavController.rootFragments?.let { (it[1] as DataObserver).viewInvisibleDataChangeListener(actionState.appInfo) }
        }
    }
    private val recentlyActionObserver = Observer<RecentlyAppDatabaseActionState> { actionState ->
        when (actionState) {
            is RecentlyAppDatabaseActionState.None -> return@Observer
            is RecentlyAppDatabaseActionState.Failed -> Toast.makeText(applicationContext, "miss database", Toast.LENGTH_SHORT).show()
            is RecentlyAppDatabaseActionState.Success -> fragNavController.rootFragments?.let {
                if(store.databaseActionData.value is DatabaseActionState.Success){
                    (it[0] as DataObserver).viewDataBaseChangeListener((store.databaseActionData.value as DatabaseActionState.Success).appList, actionState.recentlyList)
                }else{
                    databaseActionCreator.getExecute()
                }
            }
        }
    }
    private val removeActionObserver = Observer<RemoveAppActionState> { actionState ->
        when(actionState) {
            is RemoveAppActionState.None -> return@Observer
            is RemoveAppActionState.Failed -> {
                Toast.makeText(applicationContext, "database failed", Toast.LENGTH_SHORT).show()
                (fragNavController.currentFrag as DataObserver).removeAppFailedListener(actionState.packageName)
            }
            is RemoveAppActionState.Success -> (fragNavController.currentFrag as DataObserver).removeAppListener()
        }
    }
    private val changeCommandActionObserver = Observer<ChangeCommandActionState> { actionState->
        when (actionState) {
            is ChangeCommandActionState.None -> return@Observer
            is ChangeCommandActionState.Success -> (fragNavController.currentFrag as DataObserver).changeCommandListener()
            is ChangeCommandActionState.Failed -> Toast.makeText(applicationContext, actionState.errorString, Toast.LENGTH_LONG).show()
        }
    }
    private val controlFavoriteActionObserver = Observer<ControlFavoriteActionState>{ actionState ->
        when(actionState){
            is ControlFavoriteActionState.None -> return@Observer
            is ControlFavoriteActionState.Success -> fragNavController.rootFragments?.let { (it[0] as VisibleAppViewFragment).updateFavoriteListener() }
            is ControlFavoriteActionState.Failed -> Toast.makeText(applicationContext, "database error", Toast.LENGTH_LONG).show()
        }
    }
    private val controlVisibleActionObserver = Observer<ToggleVisibleActionState>{ actionState ->
        when(actionState){
            is ToggleVisibleActionState.None -> return@Observer
            is ToggleVisibleActionState.Success -> (fragNavController.currentFrag as DataObserver).updateVisibleListener()
            is ToggleVisibleActionState.Failed -> Toast.makeText(applicationContext, "database error", Toast.LENGTH_LONG).show()
        }
    }

    private fun onSetActionObserver(){
        store.databaseActionData.observe(this, databaseActionObserver)
        store.invisibleAppActionData.observe(this, invisibleAppActionObserver)
        store.recentlyActionData.observe(this, recentlyActionObserver)
        store.removeActionData.observe(this, removeActionObserver)
        store.changeCommandData.observe(this, changeCommandActionObserver)
        store.controlFavoriteData.observe(this, controlFavoriteActionObserver)
        store.controlVisibleData.observe(this, controlVisibleActionObserver)
    }

    private fun onRemoveActionObserver(){
        store.databaseActionData.removeObservers(this)
        store.recentlyActionData.removeObservers(this)
        store.invisibleAppActionData.removeObservers(this)
        store.removeActionData.removeObservers(this)
        store.changeCommandData.removeObservers(this)
        store.controlFavoriteData.removeObservers(this)
        store.controlVisibleData.removeObservers(this)
    }

    override fun onResume() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        registerReceiver(uninstallEvent, intentFilter)
        super.onResume()
    }

    override fun onResumeFragments() {
        onSetSearchTextObserver()
        onSetActionObserver()
        super.onResumeFragments()
    }

    override fun onClick(p0: View?) {
        if(p0?.id == R.id.toggleVisible){
            if(fragNavController.currentStackIndex == FragNavController.TAB1){
                fragNavController.switchTab(FragNavController.TAB2)
                toggleVisible.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_off_36))
            }else{
                fragNavController.switchTab(FragNavController.TAB1)
                toggleVisible.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_36))
            }
        }
    }

    override fun commandChangeListener(appIcon: Drawable, appName: String, packageName: String, command: String){
        commandChange(appIcon, appName, packageName, command)
    }

    private fun commandChange(appIcon: Drawable, appName: String, packageName: String, command: String){
        MaterialDialog(this).show {
            title(text = appName)
            icon(drawable = appIcon)
            message(text = "old command = $command") {
                messageTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            }
            input(hint = "new command", inputType = InputType.TYPE_CLASS_NUMBER, maxLength = 4, waitForPositiveButton = false, allowEmpty = false){ dialog, text ->
                val inputField = dialog.getInputField()
                val isSafeLength = text.length == 4
                inputField.error = if(isSafeLength) null else "You have to choose a 4-digit command"
                setActionButtonEnabled(WhichButton.POSITIVE, isSafeLength)
            }
            positiveButton {
                changeCommandActionCreator.execute(packageName, getInputField().text.toString())
            }
        }
    }

    override fun onStop() {
        unregisterReceiver(uninstallEvent)
        super.onStop()
    }

    private val uninstallEvent = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?){
            p1?.data?.schemeSpecificPart?.let { removeAppActionCreator.execute(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        onRemoveActionObserver()
        super.onDestroy()
    }
}
