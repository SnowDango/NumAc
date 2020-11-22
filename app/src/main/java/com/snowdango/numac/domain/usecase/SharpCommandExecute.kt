package com.snowdango.numac.domain.usecase

import androidx.appcompat.app.AppCompatDelegate
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.actions.command.CommandAction
import com.snowdango.numac.actions.command.CommandActionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SharpCommandExecute(private val coroutineScope: CoroutineScope) {

    sealed class CommandResult{
        object Recreate: CommandResult()
        object Road: CommandResult()
        object AppViewIntent: CommandResult()
        class Failed(val errorString: String): CommandResult()
    }

    suspend fun executeCommand(command: String): CommandResult {
        return try {
            val commandResult = sharpCommandExec(command)
            when (commandResult.first) {
                0 -> CommandResult.Recreate
                1 -> CommandResult.AppViewIntent
                2 -> CommandResult.Road
                else -> CommandResult.Failed(commandResult.second)
            }
        }catch (e: Exception){
            CommandResult.Failed("Exception")
        }
    }

    private fun sharpCommandExec(command: String): Pair<Int,String>{
        val sharpCommandList = SingletonContext.applicationContext().resources.getStringArray(R.array.sharp_command)
        return when(command){
            sharpCommandList[0] -> changeViewMode()
            sharpCommandList[1] -> launchAppView()
            sharpCommandList[2] -> roadAppList()
            else -> Pair(-1 ,"Not Found Sharp Command")
        }
    }

    private fun changeViewMode(): Pair<Int,String>{
        return try {
            coroutineScope.launch(Dispatchers.Main) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            Pair(0,"")
        }catch (e: Exception){
            Pair(-1,"Exception")
        }
    }

    private fun roadAppList(): Pair<Int,String>{
        return Pair(2,"")
    }

    private fun launchAppView():Pair<Int,String>{
        return Pair(1,"")
    }
}