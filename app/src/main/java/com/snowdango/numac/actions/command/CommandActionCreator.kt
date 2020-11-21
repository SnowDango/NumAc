package com.snowdango.numac.actions.command

import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.dispatcher.main.Dispatcher
import com.snowdango.numac.domain.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class CommandActionCreator(private val coroutineScope: CoroutineScope,private val dispatcher: Dispatcher) {

    private val sharpCommandActionFunction = SharpCommandActionFunction(coroutineScope)

    fun execute(command: String,appList: ArrayList<AppInfo>){
        coroutineScope.launch(Dispatchers.Default){
            launchCommand(command,appList)
        }
    }

    private suspend fun launchCommand(command: String, appList: ArrayList<AppInfo>){
        if(!command[0].equals("#") && command == "0000") {
            val errorStringList = SingletonContext.applicationContext().resources.getStringArray(R.array.error_log)
            try {
                val filter = appList.filter { appInfo -> appInfo.command == command }
                val pm = SingletonContext.applicationContext().packageManager
                val actionState =
                        if (filter.isNotEmpty()) {
                            val intent = pm.getLaunchIntentForPackage(filter[0].packageName)
                            if (intent != null) {
                                SingletonContext.applicationContext().startActivity(intent)
                                CommandAction(CommandActionState.Success)
                            } else CommandAction(CommandActionState.Failed(errorStringList[2]))
                        } else CommandAction(CommandActionState.Failed(errorStringList[1]))
                coroutineScope.launch(Dispatchers.Main) {
                    dispatcher.dispatchCommand(actionState)
                }
            } catch (e: Exception) {
                coroutineScope.launch(Dispatchers.Main) {
                    dispatcher.dispatchCommand(CommandAction(CommandActionState.Failed(errorStringList[0])))
                }
            }
        }else{
            val commandResult = sharpCommandActionFunction.sharpCommandExecute(command)
            if(commandResult.first){
                coroutineScope.launch(Dispatchers.Main){
                    dispatcher.dispatchCommand(CommandAction(CommandActionState.Recreate))
                }
            }else{
                coroutineScope.launch(Dispatchers.Main){
                    dispatcher.dispatchCommand(CommandAction(CommandActionState.Failed(commandResult.second)))
                }
            }
        }
    }
}