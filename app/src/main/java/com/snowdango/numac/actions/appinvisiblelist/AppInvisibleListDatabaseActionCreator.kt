package com.snowdango.numac.actions.appinvisiblelist

import android.util.Log
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppInvisibleListDatabaseActionCreator(
        private val coroutineScope: CoroutineScope,
        private val dispatcher: Dispatcher,
        private val appListDatabaseUse: AppListDatabaseUse
) {

    fun execute(){
        coroutineScope.launch(Dispatchers.IO){
            createAction()
        }
    }

    private suspend fun createAction(){
        val action = when(val result = appListDatabaseUse.getAppInVisibleList()){
            is AppListDatabaseUse.DatabaseResult.Success -> AppInvisibleListDatabaseAction(AppInvisibleListDatabaseActionState.Success(result.appList))
            is AppListDatabaseUse.DatabaseResult.Failed -> AppInvisibleListDatabaseAction(AppInvisibleListDatabaseActionState.Failed)
        }
        coroutineScope.launch(Dispatchers.Main){
            Log.d("actionCreator", "invisibleList")
            dispatcher.dispatchInVisibleDb(action)
        }
    }
}