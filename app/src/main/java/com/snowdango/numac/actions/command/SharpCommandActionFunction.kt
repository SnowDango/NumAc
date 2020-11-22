package com.snowdango.numac.actions.command

import androidx.appcompat.app.AppCompatDelegate
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharpCommandActionFunction(private val coroutineScope: CoroutineScope){

    fun sharpCommandExecute(command: String): Pair<Int,String>{
        val sharpCommandList = SingletonContext.applicationContext().resources.getStringArray(R.array.sharp_command)
        return when(command){
            sharpCommandList[0] -> changeViewMode()
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
        }catch (e:Exception){
            Pair(-1,"Exception")
        }
    }

    private fun roadAppList(): Pair<Int,String>{
        return Pair(1,"")
    }
}