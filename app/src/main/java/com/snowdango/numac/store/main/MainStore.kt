package com.snowdango.numac.store.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.applist.AppListActionState
import com.snowdango.numac.actions.command.CommandAction
import com.snowdango.numac.actions.command.CommandActionState
import com.snowdango.numac.dispatcher.Dispatcher
import kotlinx.coroutines.CoroutineScope

class MainStore(private val dispatcher: Dispatcher):
        ViewModel(), Dispatcher.AppListActionListener,Dispatcher.CommandActionListener{

    init {
        dispatcher.registerMain(this,this)
    }
    val viewModelsCoroutineScope: CoroutineScope = viewModelScope

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