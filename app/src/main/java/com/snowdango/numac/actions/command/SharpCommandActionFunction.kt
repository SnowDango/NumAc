package com.snowdango.numac.actions.command

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.activity.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class SharpCommandActionFunction(private val coroutineScope: CoroutineScope){

    fun sharpCommandExecute(command: String): Pair<Boolean,String>{
        val sharpCommandList = SingletonContext.applicationContext().resources.getStringArray(R.array.sharp_command)
        return when(command){
            sharpCommandList[0] -> changeViewMode()
            else -> Pair(false,"Not Found Sharp Command")
        }
    }

    private fun changeViewMode(): Pair<Boolean,String>{
        return try {
            coroutineScope.launch(Dispatchers.Main) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            Pair(true,"")
        }catch (e:Exception){
            Pair(false,"Exception")
        }
    }
}