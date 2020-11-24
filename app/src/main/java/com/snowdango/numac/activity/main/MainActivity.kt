package com.snowdango.numac.activity.main

import android.content.Intent
import android.content.pm.PackageInstaller
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.actions.applist.AppListActionCreator
import com.snowdango.numac.actions.applist.AppListActionState
import com.snowdango.numac.actions.command.CommandActionCreator
import com.snowdango.numac.actions.command.CommandActionState
import com.snowdango.numac.activity.appview.AppViewActivity
import com.snowdango.numac.dispatcher.main.MainDispatcher
import com.snowdango.numac.store.main.MainStore
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    private val coroutineScope: CancellableCoroutineScope = CancellableCoroutineScope()
    private val dispatcher = MainDispatcher()
    private val appListActionCreator: AppListActionCreator = AppListActionCreator(coroutineScope, dispatcher)
    private val commandActionCreator: CommandActionCreator = CommandActionCreator(coroutineScope, dispatcher)
    private val store: MainStore = MainStore(dispatcher)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roadData()

        val mainButtonController = MainButtonController( object : MainButtonController.ClickListener{
            override fun itemClickListener(string: String) {
                updateText(string)
            }
        })

        recyclerViewMain.apply {
            adapter = mainButtonController.adapter
            layoutManager = GridLayoutManager(applicationContext, 3).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }

        mainButtonController.setData(resources.getStringArray(R.array.button_string))
        errorOverlay.visibility = View.GONE

        store.appListActionData.observe(this, appListActionDataObserver)
        store.commandActionData.observe(this, commandActionDataObserver)
    }

    private fun roadData(){
        appListActionCreator.execute()
        textView.text = getString(R.string.wait_text)
        progressMaterialHorizontal.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        coroutineScope.cancel()
    }

    private val appListActionDataObserver = Observer<AppListActionState> {
        when(it){
            is AppListActionState.Failed -> Log.d("AppListAction","Failed")
            is AppListActionState.Success -> {
                progressMaterialHorizontal.visibility = View.INVISIBLE
                textView.text = getString(R.string.please_push_number)
            }
            is AppListActionState.None -> return@Observer
        }
    }

    private val commandActionDataObserver  = Observer<CommandActionState> {
        when(it){
            is CommandActionState.Success -> textView.text = getString(R.string.please_push_number)
            is CommandActionState.Failed -> errorView(it.failedState)
            is CommandActionState.Recreate -> {
                textView.text = getString(R.string.change_mode)
                recreate()
            }
            is CommandActionState.Road -> roadData()
            is CommandActionState.AppViewIntent -> startActivity(Intent(this,AppViewActivity::class.java))
            is CommandActionState.None -> return@Observer
        }
    }

    private fun errorView(errorString: String){
        textView.text = errorString
        textView.setTextColor(Color.RED)
        errorOverlay.visibility = View.VISIBLE
        coroutineScope.launch(Dispatchers.Default){
            Thread.sleep(2000)
            textView.text = getString(R.string.please_push_number)
            textView.setTextColor(getColor(R.color.fullactivityText))
            coroutineScope.launch(Dispatchers.Main) {
                errorOverlay.visibility = View.GONE
            }
        }
    }

    private fun updateText(string: String){
        val errorStringList = SingletonContext.applicationContext().resources.getStringArray(R.array.error_log)
        val textStatus = textView.text.toString()
        if(string == "clear"){
            textView.text = getString(R.string.please_push_number)
        }else{
            when {
                errorStringList.indexOf(textStatus) != -1
                        || textStatus == getString(R.string.wait_text)
                        || textStatus == getString(R.string.change_mode) -> return
                textStatus == getString(R.string.please_push_number) -> textView.text = string
                textStatus.length < 3 -> textView.text = textStatus.plus(string)
                else -> {
                    if(store.appListActionData.value is AppListActionState.Success)
                        commandActionCreator.execute(
                                textStatus.plus(string),
                                (store.appListActionData.value as AppListActionState.Success).appList
                        )
                    textView.text = getString(R.string.please_push_number)
                }
            }
        }
    }
}
