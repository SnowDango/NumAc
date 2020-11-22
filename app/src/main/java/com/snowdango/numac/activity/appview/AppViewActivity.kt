package com.snowdango.numac.activity.appview

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.snowdango.numac.R
import com.snowdango.numac.actions.database.DatabaseActionCreate
import com.snowdango.numac.actions.database.DatabaseActionState
import com.snowdango.numac.dispatcher.appview.AppViewDispatcher
import com.snowdango.numac.store.appview.AppViewStore
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.android.synthetic.main.activity_appview.*

class AppViewActivity: AppCompatActivity() {

    private val coroutineScope: CancellableCoroutineScope = CancellableCoroutineScope()
    private val dispatcher = AppViewDispatcher()
    private val databaseActionCreate = DatabaseActionCreate(coroutineScope,dispatcher)
    private val store = AppViewStore(dispatcher)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)

        databaseActionCreate.getExecute()

        val appItemController = AppItemController(object : AppItemController.AppClickListener{
            override fun appClickListener(string: String) {
                Toast.makeText(this@AppViewActivity,string,Toast.LENGTH_LONG).show()
            }
        })

        recyclerViewApp.apply {
            adapter = appItemController.adapter
            layoutManager = GridLayoutManager(applicationContext, 4).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }

        store.databaseActionData.observe(this, Observer {
            progressMaterial.visibility = View.GONE
            when(it){
                is DatabaseActionState.None -> return@Observer
                is DatabaseActionState.Failed -> Toast.makeText(this,"miss database",Toast.LENGTH_SHORT).show()
                is DatabaseActionState.Success -> appItemController.setData(it.appList)
            }
        })
    }
}
