package com.snowdango.numac.activity.main

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

        roadData()

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
        errorOverlay.visibility = View.GONE

        store.appListActionData.observe(this, appListActionDataObserver)
        store.commandActionData.observe(this, commandActionDataObserver)
    }

    private fun roadData(){
        appListActionCreator.execute()
        textView.text = getString(R.string.wait_text)
        progressMaterial.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        coroutineScope.cancel()
    }

    private val appListActionDataObserver = Observer<AppListActionState> {
        when(it){
            is AppListActionState.Failed -> Log.d("AppListAction","Failed")
            is AppListActionState.Success -> {
                progressMaterial.visibility = View.INVISIBLE
                textView.text = getString(R.string.please_push_number)
            }
            is AppListActionState.None -> return@Observer
        }
    }

    private val commandActionDataObserver  = Observer<CommandActionState> {
        when(it){
            is CommandActionState.Success -> textView.text = getString(R.string.please_push_number)
            is CommandActionState.Failed -> errorView(it.failedState)
            is CommandActionState.Recreate -> recreate()
            is CommandActionState.Road -> roadData()
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
                errorStringList.indexOf(textStatus) != -1 || textStatus == getString(R.string.wait_text) -> return
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