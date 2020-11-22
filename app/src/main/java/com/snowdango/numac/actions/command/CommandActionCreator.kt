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
            actionCreate(command, appList)
        }
    }


    private suspend fun actionCreate(command: String,appList: ArrayList<AppInfo>){
        val action: CommandAction = when {
            command.indexOf("#") == -1 && command != "0000" -> launchApp(command,appList)
            else -> executeCommand(command)
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchCommand(action)
        }
    }

    private suspend fun launchApp(command: String,appList: ArrayList<AppInfo>): CommandAction{
        val errorStringList = SingletonContext.applicationContext().resources.getStringArray(R.array.error_log)
        return try {
            val filter = appList.filter { appInfo -> appInfo.command == command }
            val pm = SingletonContext.applicationContext().packageManager
            if (filter.isNotEmpty()) {
                val intent = pm.getLaunchIntentForPackage(filter[0].packageName)
                if (intent != null) {
                    SingletonContext.applicationContext().startActivity(intent)
                    CommandAction(CommandActionState.Success)
                } else CommandAction(CommandActionState.Failed(errorStringList[2]))
            } else CommandAction(CommandActionState.Failed(errorStringList[1]))
        } catch (e: Exception) {
            CommandAction(CommandActionState.Failed(errorStringList[0]))
        }
    }

    private suspend fun executeCommand(command: String): CommandAction{
        return try {
            val commandResult = sharpCommandActionFunction.sharpCommandExecute(command)
            when (commandResult.first) {
                0 -> CommandAction(CommandActionState.Recreate)
                1 -> CommandAction(CommandActionState.Road)
                else -> CommandAction(CommandActionState.Failed(commandResult.second))
            }
        }catch (e: Exception){
            CommandAction(CommandActionState.Failed("Exception"))
        }
    }
}