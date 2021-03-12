package com.snowdango.numac.actions.applistdb

import android.util.Log
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListDatabaseActionCreator(
        private val coroutineScope: CoroutineScope,
        private val dispatcher: Dispatcher,
        private val appListDatabaseUse: AppListDatabaseUse) {

    fun getExecute(){
        coroutineScope.launch(Dispatchers.IO){
            createAction()
        }
    }

    private suspend fun createAction(){
        try {
            val action = when (val result = appListDatabaseUse.getAppVisibleList()) {
                is AppListDatabaseUse.DatabaseResult.Success -> DatabaseAction(DatabaseActionState.Success(result.appList))
                is AppListDatabaseUse.DatabaseResult.Failed -> DatabaseAction(DatabaseActionState.Failed)
            }
            coroutineScope.launch(Dispatchers.Main) {
                dispatcher.dispatchDatabase(action)
            }
        }catch (e: Exception){
            Log.e("data action", e.toString())
        }
    }
}