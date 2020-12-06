package com.snowdango.numac.actions.applistdb

import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListDatabaseActionCreator(
        private val coroutineScope: CancellableCoroutineScope,
        private val dispatcher: Dispatcher,
        private val appListDatabaseUse: AppListDatabaseUse) {

    fun getExecute(){
        coroutineScope.launch(Dispatchers.IO){
            createAction()
        }
    }

    private suspend fun createAction(){
        val action = when(val result = appListDatabaseUse.getAppList()){
            is AppListDatabaseUse.DatabaseResult.Success -> DatabaseAction(DatabaseActionState.Success(result.appList))
            is AppListDatabaseUse.DatabaseResult.Failed -> DatabaseAction(DatabaseActionState.Failed)
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchDatabase(action)
        }
    }
}