package com.snowdango.numac.domain.usecase

import com.snowdango.numac.R
import com.snowdango.numac.SingletonContext
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import java.lang.Exception

class LaunchApp {

    sealed class LaunchResult{
        object Success: LaunchResult()
        class Failed(val failedState: String): LaunchResult()
    }

    suspend fun launchApplication(command: String,appList: ArrayList<AppInfo>): LaunchResult {
        val errorStringList = SingletonContext.applicationContext().resources.getStringArray(R.array.error_log)
        return try {
            val filter = appList.filter { appInfo -> appInfo.command == command }
            val pm = SingletonContext.applicationContext().packageManager
            if (filter.isNotEmpty()) {
                val intent = pm.getLaunchIntentForPackage(filter[0].packageName)
                if (intent != null) {
                    SingletonContext.applicationContext().startActivity(intent)
                    LaunchResult.Success
                } else LaunchResult.Failed(errorStringList[2])
            } else LaunchResult.Failed(errorStringList[1])
        } catch (e: Exception) {
            LaunchResult.Failed(errorStringList[0])
        }
    }
}