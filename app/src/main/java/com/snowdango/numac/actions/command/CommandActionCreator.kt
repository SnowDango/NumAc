package com.snowdango.numac.actions.command

import com.snowdango.numac.dispatcher.main.MainDispatcher
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.domain.usecase.LaunchApp
import com.snowdango.numac.domain.usecase.SharpCommandExecute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommandActionCreator(private val coroutineScope: CoroutineScope,private val mainDispatcher: MainDispatcher) {

    private val launchApp = LaunchApp()
    private val sharpCommandExecute = SharpCommandExecute(coroutineScope)

    fun execute(command: String,appList: ArrayList<AppInfo>){
        coroutineScope.launch(Dispatchers.Default){
            actionCreate(command, appList)
        }
    }

    private suspend fun actionCreate(command: String,appList: ArrayList<AppInfo>){
        val result: Any = when {
            command.indexOf("#") == -1 && command != "0000" -> launchApp.launchApplication(command,appList)
            else -> sharpCommandExecute.executeCommand(command)
        }

        val action: CommandAction = when(result) {
            is LaunchApp.LaunchResult.Success -> CommandAction(CommandActionState.Success)
            is LaunchApp.LaunchResult.Failed -> CommandAction(CommandActionState.Failed(result.failedState))
            is SharpCommandExecute.CommandResult.Recreate -> CommandAction(CommandActionState.Recreate)
            is SharpCommandExecute.CommandResult.Road -> CommandAction(CommandActionState.Road)
            is SharpCommandExecute.CommandResult.AppViewIntent -> CommandAction(CommandActionState.AppViewIntent)
            is SharpCommandExecute.CommandResult.Failed -> CommandAction(CommandActionState.Failed(result.errorString))
            else -> CommandAction(CommandActionState.Failed("Exception"))
        }

        coroutineScope.launch(Dispatchers.Main){
            mainDispatcher.dispatchCommand(action)
        }
    }
}