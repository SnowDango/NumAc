package com.snowdango.numac.activity.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.snowdango.numac.R
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
                checkText(string)
            }
        })

        recyclerViewMain.apply {
            adapter = mainButtonController.adapter
            layoutManager = GridLayoutManager(applicationContext, 3).apply {
                orientation = GridLayoutManager.VERTICAL
            }
        }

        mainButtonController.setData(resources.getStringArray(R.array.button_string))
        observeValue()
    }

    // dataの取得
    private fun roadData(){
        appListActionCreator.execute()
        textView.text = getString(R.string.wait_text)
        progressMaterialHorizontal.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // storeのobserver
    private fun observeValue(){
        val appListActionDataObserver = Observer<AppListActionState> {
            when(it){
                is AppListActionState.Failed -> Toast.makeText(this,"AppListAction Failed",Toast.LENGTH_SHORT).show()
                is AppListActionState.Success -> {
                    progressMaterialHorizontal.visibility = View.INVISIBLE
                    textView.text = getString(R.string.please_push_number)
                }
                is AppListActionState.None -> return@Observer
            }
        }

        val commandActionDataObserver  = Observer<CommandActionState> {
            when(it){
                is CommandActionState.Success -> textView.text = getString(R.string.please_push_number)
                is CommandActionState.Failed -> errorViewBefore(it.failedState)
                is CommandActionState.Recreate -> recreate()
                is CommandActionState.Road -> roadData()
                is CommandActionState.AppViewIntent -> startActivity(Intent(this,AppViewActivity::class.java))
                is CommandActionState.None -> return@Observer
            }
        }

        store.appListActionData.observe(this, appListActionDataObserver)
        store.commandActionData.observe(this, commandActionDataObserver)
    }


    // error::before
    private fun errorViewBefore(errorString: String){
        textView.text = errorString
        textView.setTextColor(Color.RED)
        errorViewAfter()
    }

    // error::after
    private fun errorViewAfter(){
        coroutineScope.launch(Dispatchers.Default){
            Thread.sleep(2000)
            textView.text = getString(R.string.please_push_number)
            textView.setTextColor(getColor(R.color.fullactivityText))
        }
    }

    // textViewの更新パターン
    private fun checkText(text: String){
        if(text == "clear"){
            Log.d( TAG , "set please update" )
            textView.text = getString(R.string.please_push_number)
        }else{
            val textStatus = textView.text.toString()
            when{
                textStatus.length < 3 -> textView.text = textStatus.plus(text)
                textStatus == getString(R.string.please_push_number) -> textView.text = text
                textStatus.length == 3 -> commandExec(textStatus.plus(text))
                else -> return
            }
        }
    }

    // commandの実行
    private fun commandExec(command: String){
        if(store.appListActionData.value is AppListActionState.Success)
            commandActionCreator.execute(
                    command,
                    (store.appListActionData.value as AppListActionState.Success).appList
            )
        Log.d( TAG , "set please command" )
        textView.text = getString(R.string.please_push_number)
    }

    companion object{
        const val TAG = "MainActivity"
    }
}
