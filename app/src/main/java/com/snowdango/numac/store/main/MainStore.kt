package com.snowdango.numac.store.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.applist.AppListActionState
import com.snowdango.numac.actions.command.CommandAction
import com.snowdango.numac.actions.command.CommandActionState
import com.snowdango.numac.dispatcher.main.MainDispatcher

class MainStore(private val mainDispatcher: MainDispatcher):
        ViewModel(), MainDispatcher.AppListActionListener,MainDispatcher.CommandActionListener{

    init {
        mainDispatcher.register(this,this)
    }

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
        mainDispatcher.unregister(this,this)
    }

    override fun on(action: CommandAction) = updateCommand(action)

}