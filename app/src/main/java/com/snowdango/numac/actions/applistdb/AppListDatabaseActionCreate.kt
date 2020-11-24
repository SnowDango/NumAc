package com.snowdango.numac.actions.applistdb

import com.snowdango.numac.dispatcher.appview.AppViewDispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListDatabaseActionCreate(private val coroutineScope: CoroutineScope, private val dispatcher: AppViewDispatcher) {

    private val appListDatabaseUse: AppListDatabaseUse = AppListDatabaseUse()

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