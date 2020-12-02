package com.snowdango.numac.domain.usecase

import androidx.appcompat.app.AppCompatDelegate
import com.snowdango.numac.R
import com.snowdango.numac.NumApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

enum class CommandList{
    ChangeView,
    LaunchAppView,
    RoadAppList,
    Error
}

class SharpCommandExecute(private val coroutineScope: CoroutineScope) {

    sealed class CommandResult{
        object Recreate: CommandResult()
        object Road: CommandResult()
        object AppViewIntent: CommandResult()
        class Failed(val errorString: String): CommandResult()
        object None: CommandResult()
    }

    fun executeCommand(command: String): CommandResult {
        return try {
            when (sharpCommandExec(command)) {
                CommandList.ChangeView -> CommandResult.Recreate
                CommandList.LaunchAppView -> CommandResult.AppViewIntent
                CommandList.RoadAppList -> CommandResult.Road
                CommandList.Error -> CommandResult.Failed("NotFoundCommand")
            }
        }catch (e: Exception){
            CommandResult.Failed("Exception")
        }
    }

    private fun sharpCommandExec(command: String): CommandList{
        val sharpCommandList = NumApp.singletonContext().resources.getStringArray(R.array.sharp_command)
        return when(command){
            sharpCommandList[0] -> changeViewMode()
            sharpCommandList[1] -> launchAppView()
            sharpCommandList[2] -> roadAppList()
            else -> CommandList.Error
        }
    }

    private fun changeViewMode(): CommandList{
        return try {
            coroutineScope.launch(Dispatchers.Main) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            CommandList.ChangeView
        }catch (e: Exception){
            CommandList.Error
        }
    }

    private fun roadAppList(): CommandList{
        return CommandList.RoadAppList
    }

    private fun launchAppView(): CommandList{
        return CommandList.LaunchAppView
    }
}