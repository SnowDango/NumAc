package com.snowdango.numac.actions.applist

import com.snowdango.numac.dispatcher.main.MainDispatcher
import com.snowdango.numac.domain.usecase.AppListCreate
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListActionCreator(private val coroutineScope: CoroutineScope, private val mainDispatcher: MainDispatcher) {

    private val appListCreate: AppListCreate = AppListCreate()
    private val appListDatabaseUse: AppListDatabaseUse = AppListDatabaseUse()

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
                AppListAction(AppListActionState.Success(actionResult.listAppList))
            }
            is AppListCreate.CreateResult.Failed -> AppListAction(AppListActionState.Failed)
        }
        coroutineScope.launch(Dispatchers.Main){
            mainDispatcher.dispatchAppList(action)
        }
    }
}