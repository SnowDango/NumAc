package com.snowdango.numac.domain.usecase

import android.content.Intent
import android.util.Log
import com.snowdango.numac.R
import com.snowdango.numac.NumApp
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import java.lang.Exception

class LaunchApp {

    sealed class LaunchResult{
        class Success(val intent: Intent): LaunchResult()
        class Failed(val failedState: String): LaunchResult()
    }

    fun launchApplication(command: String,appList: ArrayList<AppInfo>): LaunchResult {
        val errorStringList = NumApp.singletonContext().resources.getStringArray(R.array.error_log)
        return try {
            val filter = appList.filter { appInfo -> appInfo.command == command }
            Log.d("launchApp", filter.toString())
            val pm = NumApp.singletonContext().packageManager
            if (filter.isNotEmpty()) {
                val intent = pm.getLaunchIntentForPackage(filter[0].packageName)
                if (intent != null) {
                    LaunchResult.Success(intent)
                } else LaunchResult.Failed(errorStringList[2])
            } else LaunchResult.Failed(errorStringList[1])
        } catch (e: Exception) {
            LaunchResult.Failed(errorStringList[0])
        }
    }
}