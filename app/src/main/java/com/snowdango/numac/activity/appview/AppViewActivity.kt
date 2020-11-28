package com.snowdango.numac.activity.appview

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.snowdango.numac.R
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseAction
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo
import com.snowdango.numac.dispatcher.appview.AppViewDispatcher
import com.snowdango.numac.store.appview.AppViewStore
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.android.synthetic.main.activity_appview.*

class AppViewActivity: AppCompatActivity() {

    private val coroutineScope: CancellableCoroutineScope = CancellableCoroutineScope()
    private val dispatcher = AppViewDispatcher()
    private val databaseActionCreate = AppListDatabaseActionCreator(coroutineScope,dispatcher)
    private val recentlyAppDatabaseActionCreator = RecentlyAppDatabaseActionCreator(coroutineScope,dispatcher)
    private val store = AppViewStore(dispatcher)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)

        databaseActionCreate.getExecute() // databaseから持ってくる

        val appItemController = AppItemController(object : AppItemController.AppClickListener{
            override fun appClickListener(string: String) {
                val intent = packageManager.getLaunchIntentForPackage(string)
                intent?.let {
                    recentlyAppDatabaseActionCreator.execute(0,string)
                    startActivity(intent)
                }
            }
        })

        recyclerViewApp.apply {
            adapter = appItemController.adapter
            layoutManager = GridLayoutManager(applicationContext, 4).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }

        observeValue(appItemController)
    }

    private fun observeValue(appItemController: AppItemController){
        val databaseObserve =  Observer<DatabaseActionState>{
            when(it){
                is DatabaseActionState.None -> return@Observer
                is DatabaseActionState.Failed -> Toast.makeText(this,"miss database",Toast.LENGTH_SHORT).show()
                is DatabaseActionState.Success ->
                    if(store.recentlyActionData.value is RecentlyAppDatabaseActionState.Success){
                        appItemController.setData(it.appList,(store.recentlyActionData.value as RecentlyAppDatabaseActionState.Success).recentlyList)
                    }else{
                        recentlyAppDatabaseActionCreator.execute(1,"")
                    }
            }
        }

        val recentlyObserve = Observer<RecentlyAppDatabaseActionState>{
            when(it){
                is RecentlyAppDatabaseActionState.None -> return@Observer
                is RecentlyAppDatabaseActionState.Failed -> Toast.makeText(this,"miss database",Toast.LENGTH_SHORT).show()
                is RecentlyAppDatabaseActionState.Success ->
                    if(store.databaseActionData.value is DatabaseActionState.Success){
                        appItemController.setData((store.databaseActionData.value as DatabaseActionState.Success).appList,it.recentlyList)
                    }
            }
        }

        store.databaseActionData.observe(this,databaseObserve)
        store.recentlyActionData.observe(this,recentlyObserve)
    }
}
