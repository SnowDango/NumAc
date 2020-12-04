package com.snowdango.numac.actions.applist

import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListCreate
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListActionCreator(
        private val coroutineScope: CoroutineScope,
        private val dispatcher: Dispatcher,
        private val appListCreate: AppListCreate,
        private val appListDatabaseUse: AppListDatabaseUse
) {

    fun execute(){
        coroutineScope.launch(Dispatchers.Default){
            actionCreate()
        }
    }

    private suspend fun actionCreate(){

        val action = when(val actionResult = appListCreate.listCreate()){
            is AppListCreate.CreateResult.Success ->{
                coroutineScope.launch(Dispatchers.IO){
                    appListDatabaseUse.appListInsert(actionResult.listAppList)
                }
                when(val getResult = appListDatabaseUse.getAppList()){
                    is AppListDatabaseUse.DatabaseResult.Success -> AppListAction(AppListActionState.Success(getResult.appList))
                    is AppListDatabaseUse.DatabaseResult.Failed -> AppListAction(AppListActionState.Failed)
                }
            }
            is AppListCreate.CreateResult.Failed -> AppListAction(AppListActionState.Failed)
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchAppList(action)
        }
    }
}