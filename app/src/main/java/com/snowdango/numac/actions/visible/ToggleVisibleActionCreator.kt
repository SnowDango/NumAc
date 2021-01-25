package com.snowdango.numac.actions.visible

import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToggleVisibleActionCreator(
        private val coroutineScope: CoroutineScope,
        private val dispatcher: Dispatcher,
        private val appListDatabaseUse: AppListDatabaseUse
        ) {

    fun execute(packageName: String){
        coroutineScope.launch(Dispatchers.IO){
            updateVisible(packageName)
        }
    }

    private suspend fun updateVisible(packageName: String){
        val action = when(appListDatabaseUse.updateVisible(packageName)){
            is AppListDatabaseUse.DatabaseResult.Success -> ToggleVisibleAction(ToggleVisibleActionState.Success)
            is AppListDatabaseUse.DatabaseResult.Failed -> ToggleVisibleAction(ToggleVisibleActionState.Failed("DataBase Exception"))
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchVisibleControl(action)
        }
    }
}