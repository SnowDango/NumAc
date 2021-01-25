package com.snowdango.numac.store.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.applist.AppListActionState
import com.snowdango.numac.actions.command.CommandAction
import com.snowdango.numac.actions.command.CommandActionState
import com.snowdango.numac.dispatcher.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainStore(private val dispatcher: Dispatcher):
        ViewModel(), Dispatcher.AppListActionListener,Dispatcher.CommandActionListener{

    init {
        dispatcher.registerMain(this,this)
    }
    private val viewModelJob: Job = Job()
    val viewModelsCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val appListActionData: LiveData<AppListActionState> = MutableLiveData<AppListActionState>().apply { value = AppListActionState.None }
    val commandActionData: LiveData<CommandActionState> = MutableLiveData<CommandActionState>().apply { value = CommandActionState.None }

    override fun on(action: AppListAction) = updateAppList(action)

    private fun updateAppList(action: AppListAction) {
        Log.d("AppListAction","update store")
        (appListActionData as MutableLiveData<AppListActionState>).value = action.state
    }

    private fun updateCommand(action: CommandAction) {
        Log.d("AppListAction","update store")
        (commandActionData as MutableLiveData<CommandActionState>).value = action.state
    }

    override fun onCleared() {
        super.onCleared()
        dispatcher.unregisterMain(this,this)
    }

    override fun on(action: CommandAction) = updateCommand(action)

}