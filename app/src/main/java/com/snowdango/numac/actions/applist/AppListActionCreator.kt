package com.snowdango.numac.actions.applist

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.dispatcher.main.Dispatcher
import com.snowdango.numac.domain.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListActionCreator(private val coroutineScope: CoroutineScope, private val dispatcher: Dispatcher) {

    fun execute(){
        Log.d("AppListAction","execute")
        coroutineScope.launch(Dispatchers.Default){
            listCreate()
        }
    }

    private suspend fun listCreate(){
        try {
            Log.d("AppListAction","listCreate")
            val context = SingletonContext.applicationContext()
            val pm = context.packageManager
            val pckInfoList: List<PackageInfo> = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)
            val appInfoList: ArrayList<AppInfo> = ArrayList()
            val commandAlreadyList: ArrayList<String> = ArrayList()
            for (pckInfo in pckInfoList) {
                if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                    val newCommand = randomCommand(commandAlreadyList)
                    appInfoList.add(AppInfo(
                            pckInfo.applicationInfo.loadLabel(pm) as String,
                            pckInfo.packageName,
                            newCommand
                    ))
                    commandAlreadyList.add(newCommand)
                }
            }
            coroutineScope.launch(Dispatchers.Main) {
                dispatcher.dispatchAppList(AppListAction(AppListActionState.Success(appInfoList)))
            }
        }catch (e:Exception){
            Log.d("AppListAction",e.toString())
            coroutineScope.launch(Dispatchers.Main) {
                dispatcher.dispatchAppList(AppListAction(AppListActionState.Failed))
            }
        }
    }

    private fun randomCommand(commandAlreadyList: ArrayList<String>): String{
        val newCommand = (1000..9999).random().toString()
        return if (commandAlreadyList.indexOf(newCommand) == -1) {
            newCommand
        } else {
            randomCommand(commandAlreadyList)
        }
    }}