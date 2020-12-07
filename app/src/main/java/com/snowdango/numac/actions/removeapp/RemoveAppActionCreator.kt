package com.snowdango.numac.actions.removeapp

import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemoveAppActionCreator(private val coroutineScope: CoroutineScope,
                             private val dispatcher: Dispatcher,
                             private val databaseUse: AppListDatabaseUse
) {

    fun execute(packageName: String){
        coroutineScope.launch(Dispatchers.IO){
            removeApp(packageName)
        }
    }

    private fun removeApp(packageName: String){
        val action = when(databaseUse.removeApp(packageName)){
            is AppListDatabaseUse.DatabaseResult.Success -> RemoveAppAction(RemoveAppActionState.Success)
            is AppListDatabaseUse.DatabaseResult.Failed -> RemoveAppAction(RemoveAppActionState.Failed(packageName))
        }

        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchRemoveApp(action)
        }
    }
}