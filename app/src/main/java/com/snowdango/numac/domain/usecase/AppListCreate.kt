package com.snowdango.numac.domain.usecase

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.snowdango.numac.NumApp
import com.snowdango.numac.data.repository.dao.entity.AppInfo

class AppListCreate {

    sealed class CreateResult{
        class Success(val listAppList: ArrayList<AppInfo>): CreateResult()
        object Failed : CreateResult()
    }

    fun listCreate(): CreateResult{
        try {
            val pm =  NumApp.singletonContext().packageManager
            val pckInfoList: List<PackageInfo> = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)
            val appInfoList: ArrayList<AppInfo> = ArrayList()
            val commandAlreadyList: ArrayList<String> = ArrayList()
            for (pckInfo in pckInfoList) {
                if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                    val newCommand = randomCommand(commandAlreadyList)
                    appInfoList.add(AppInfo(
                            pckInfo.applicationInfo.loadLabel(pm) as String,
                            pckInfo.packageName,
                            newCommand,
                            0,
                            0
                    ))
                    commandAlreadyList.add(newCommand)
                }
            }
            return CreateResult.Success(appInfoList)
        }catch (e:Exception){
            return CreateResult.Failed
        }
    }

    private fun randomCommand(commandAlreadyList: ArrayList<String>): String{
        val newCommand = (1000..9999).random().toString()
        return if (commandAlreadyList.indexOf(newCommand) == -1) {
            newCommand
        } else {
            randomCommand(commandAlreadyList)
        }
    }
}