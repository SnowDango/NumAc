package com.snowdango.numac.activity.main

import android.content.res.ColorStateList
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
import com.snowdango.numac.dispatcher.main.Dispatcher
import com.snowdango.numac.store.main.AppListStore
import com.snowdango.numac.utility.CancellableCoroutineScope

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    private val coroutineScope: CancellableCoroutineScope = CancellableCoroutineScope()
    private val dispatcher = Dispatcher()
    private val appListActionCreator: AppListActionCreator = AppListActionCreator(coroutineScope, dispatcher)
    private val commandActionCreator: CommandActionCreator = CommandActionCreator(coroutineScope, dispatcher)
    private val store: AppListStore = AppListStore(dispatcher)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appListActionCreator.execute()

        val mainButtonController = MainButtonController( object : MainButtonController.ClickListener{
            override fun itemClickListener(string: String) {
                updateText(string)
            }
        })

        recyclerView.apply {
            adapter = mainButtonController.adapter
            layoutManager = GridLayoutManager(applicationContext, 3).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }

        mainButtonController.setData(resources.getStringArray(R.array.button_string))
        progressMaterial.visibility = View.VISIBLE
        errorOverlay.visibility = View.GONE

        store.appListActionData.observe(this, Observer {
            appListActionDataObserver(it)
        })
        store.commandActionData.observe(this, Observer {
            commandActionDataObserver(it)
        })
    }

    override fun onPause() {
        super.onPause()
        coroutineScope.cancel()
    }

    private fun appListActionDataObserver(state: AppListActionState){
        when(state){
            is AppListActionState.Failed -> Log.d("AppListAction","Failed")
            is AppListActionState.Success -> progressMaterial.visibility = View.INVISIBLE
            is AppListActionState.None -> return
        }
    }

    private fun commandActionDataObserver(state: CommandActionState){
        when(state){
            is CommandActionState.Success -> textView.text = getString(R.string.please_push_number)
            is CommandActionState.Failed -> errorView(state.failedState)
            is CommandActionState.None -> return
            is CommandActionState.Recreate -> recreate()
        }
    }

    private fun errorView(errorString: String){
        textView.text = errorString
        textView.setTextColor(Color.RED)
        errorOverlay.visibility = View.VISIBLE
        coroutineScope.launch(Dispatchers.Default){
            Thread.sleep(2000)
            textView.text = getString(R.string.please_push_number)
            textView.setTextColor(Color.BLACK)
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
                errorStringList.indexOf(textStatus) != -1 -> return
                textStatus == getString(R.string.please_push_number) -> textView.text = string
                textStatus.length < 3 -> textView.text = textStatus.plus(string)
                else -> {
                    if(store.appListActionData.value is AppListActionState.Success)
                        commandActionCreator.execute(textStatus.plus(string), (store.appListActionData.value as AppListActionState.Success).appList)
                    textView.text = getString(R.string.please_push_number)
                }
            }
        }
    }
}