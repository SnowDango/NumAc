package com.snowdango.numac.activity.appview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.snowdango.numac.R
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionState
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionState
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionState
import com.snowdango.numac.actions.visible.ToggleVisibleActionState
import com.snowdango.numac.activity.appview.visible.VisibleAppViewFragment
import com.snowdango.numac.store.appview.AppViewStore
import kotlinx.android.synthetic.main.activity_appview.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AppViewActivity: AppCompatActivity(){

    private val store: AppViewStore by viewModel()
    private val removeAppActionCreator: RemoveAppActionCreator by inject { parametersOf(store.viewModelCoroutineScope) } //アプリ削除
    private var targetFragment = VisibleAppViewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)
        toggleVisible.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_24))

        // fragment
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameView,targetFragment)
        fragmentTransaction.commit()

        onSetSearchTextObserver()
        onSetActionObserver()
    }

    private fun onSetSearchTextObserver(){
        searchView.setOnQueryTextListener(object: android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText != StringBuilder().toString()) {
                        targetFragment.searchViewListener(newText.toString())
                    } else {
                        targetFragment.onTextQueryChangeEmpty()
                    }
                }?: targetFragment.onTextQueryChangeEmpty()
                return true
            }
        })
    }

    private fun onSetActionObserver(){
        store.databaseActionData.observe(this, Observer {
            when (it) {
                is DatabaseActionState.None -> return@Observer
                is DatabaseActionState.Failed -> Toast.makeText(applicationContext, "miss database", Toast.LENGTH_SHORT).show()
                is DatabaseActionState.Success -> targetFragment.viewDataBaseChangeListener(it)
            }
        })
        store.recentlyActionData.observe(this, Observer {
            when (it) {
                is RecentlyAppDatabaseActionState.None -> return@Observer
                is RecentlyAppDatabaseActionState.Failed -> Toast.makeText(applicationContext, "miss database", Toast.LENGTH_SHORT).show()
                is RecentlyAppDatabaseActionState.Success -> targetFragment.recentlyAppDataBaseListener(it)
            }
        })
        store.removeActionData.observe(this, Observer {
            when(it) {
                is RemoveAppActionState.None -> return@Observer
                is RemoveAppActionState.Failed -> {
                    Toast.makeText(applicationContext,"database failed", Toast.LENGTH_SHORT).show()
                    targetFragment.removeAppFailedListener(it)
                }
                is RemoveAppActionState.Success -> targetFragment.removeAppListener()
            }
        })
        store.changeCommandData.observe(this, Observer {
            when (it) {
                is ChangeCommandActionState.None -> return@Observer
                is ChangeCommandActionState.Success -> targetFragment.changeCommandListener()
                is ChangeCommandActionState.Failed -> Toast.makeText(applicationContext, it.errorString, Toast.LENGTH_LONG).show()
            }
        })
        store.controlFavoriteData.observe(this, Observer {
            when(it){
                is ControlFavoriteActionState.None -> return@Observer
                is ControlFavoriteActionState.Success -> targetFragment.changeFavoriteListener()
                is ControlFavoriteActionState.Failed -> Toast.makeText(applicationContext,"database error", Toast.LENGTH_LONG).show()
            }
        })
        store.controlVisibleData.observe(this, Observer {
            when(it){
                is ToggleVisibleActionState.None -> return@Observer
                is ToggleVisibleActionState.Success -> targetFragment.changeVisibleListener()
                is ToggleVisibleActionState.Failed -> Toast.makeText(applicationContext,"database error", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        registerReceiver(uninstallEvent,intentFilter)
        super.onResume()
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

    override fun onDestroy() {
        super.onDestroy()
    }
}
