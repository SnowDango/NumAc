package com.snowdango.numac.actions.command

import android.util.Log
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.domain.usecase.LaunchApp
import com.snowdango.numac.domain.usecase.SharpCommandExecute
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommandActionCreator(
        private val coroutineScope: CoroutineScope,
        private val dispatcher: Dispatcher,
        private val launchApp: LaunchApp,
        private val sharpCommandExecute: SharpCommandExecute
) {

    fun execute(command: String,appList: ArrayList<AppInfo>){
        Log.d("actionCreator",command)
        coroutineScope.launch(Dispatchers.Default){
            Log.d("coroutine default", command)
            actionCreate(command, appList)
        }
    }

    private suspend fun actionCreate(command: String,appList: ArrayList<AppInfo>){
        //#が含まれているか0000だった場合sharpCommandにする
        val result: Any = when {
            command.indexOf("#") == -1 && command != "0000" -> launchApp.launchApplication(command,appList)
            else -> sharpCommandExecute.executeCommand(command)
        }
        val action: CommandAction = when(result) {
            is LaunchApp.LaunchResult.Success -> CommandAction(CommandActionState.Success(result.intent))
            is LaunchApp.LaunchResult.Failed -> CommandAction(CommandActionState.Failed(result.failedState))
            is SharpCommandExecute.CommandResult.Recreate -> CommandAction(CommandActionState.None)
            is SharpCommandExecute.CommandResult.Road -> CommandAction(CommandActionState.Road)
            is SharpCommandExecute.CommandResult.AppViewIntent -> CommandAction(CommandActionState.AppViewIntent)
            is SharpCommandExecute.CommandResult.Failed -> CommandAction(CommandActionState.Failed(result.errorString))
            is SharpCommandExecute.CommandResult.None -> CommandAction(CommandActionState.None)
            else -> CommandAction(CommandActionState.Failed("Exception"))
        }
        coroutineScope.launch(Dispatchers.Main){
            Log.d("action","dispatch")
            dispatcher.dispatchCommand(action)
        }
    }
}