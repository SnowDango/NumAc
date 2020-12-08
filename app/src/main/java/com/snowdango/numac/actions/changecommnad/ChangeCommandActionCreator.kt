package com.snowdango.numac.actions.changecommnad

import com.snowdango.numac.NumApp
import com.snowdango.numac.R
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ChangeCommandActionCreator(private val coroutineScope: CoroutineScope, private val dispatcher: Dispatcher,
                                 private val appListDatabaseUse: AppListDatabaseUse) {

    fun execute(packageName: String,command: String){
        coroutineScope.launch(Dispatchers.IO) {
            changeCommand(packageName, command)
        }
    }

    private suspend fun changeCommand(packageName: String,command: String){
        val action = try{
            val getResult = appListDatabaseUse.getAppList()
            val appList = (getResult as AppListDatabaseUse.DatabaseResult.Success).appList
            if(NumApp.singletonContext().resources.getStringArray(R.array.sharp_command).indexOf(command) == -1){
                if (appList.none { it.command == command }){
                    appListDatabaseUse.updateCommand(packageName, command)
                    ChangeCommandAction(ChangeCommandActionState.Success)
                }else{
                    ChangeCommandAction(ChangeCommandActionState.Failed("already used command: ${appList.find { it.command == command }?.appName}"))
                }
            }else{
                ChangeCommandAction(ChangeCommandActionState.Failed("can not use command"))
            }
        }catch (e: Exception){
            ChangeCommandAction(ChangeCommandActionState.Failed("error database"))
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchChangeCommand(action)
        }
    }
}