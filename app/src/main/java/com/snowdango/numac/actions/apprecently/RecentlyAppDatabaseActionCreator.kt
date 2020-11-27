package com.snowdango.numac.actions.apprecently

import android.util.Log
import com.snowdango.numac.dispatcher.appview.AppViewDispatcher
import com.snowdango.numac.domain.usecase.SaveRecentlyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyAppDatabaseActionCreator(private val coroutineScope: CoroutineScope, private val appViewDispatcher: AppViewDispatcher) {

    fun execute(mode: Int,packageName: String){
        coroutineScope.launch(Dispatchers.Default){
            when(mode) {
                0 -> updateRecentlyApp(packageName)
                1 -> getRecentlyApp()
            }
        }
    }


    private suspend fun updateRecentlyApp(packageName: String){
        val actionState =
                when(val result = SaveRecentlyApp().updateRecentlyAppList(packageName)){
                    is SaveRecentlyApp.SaveRecentlyAppResult.Success ->  RecentlyAppDatabaseActionState.Success(result.recentlyAppList)
                    is SaveRecentlyApp.SaveRecentlyAppResult.Failed -> RecentlyAppDatabaseActionState.Failed
                }

        coroutineScope.launch(Dispatchers.Main) {
            appViewDispatcher.dispatchRecently(RecentlyAppDatabaseAction(actionState))
        }
    }

    private suspend fun getRecentlyApp(){
        val actionState =
                when(val result = SaveRecentlyApp().getRecentlyAppList()){
                    is SaveRecentlyApp.SaveRecentlyAppResult.Success -> RecentlyAppDatabaseActionState.Success(result.recentlyAppList)
                    is SaveRecentlyApp.SaveRecentlyAppResult.Failed -> RecentlyAppDatabaseActionState.Failed
                }
        Log.d("getRecently",actionState.toString())
        coroutineScope.launch(Dispatchers.Main){
            appViewDispatcher.dispatchRecently(RecentlyAppDatabaseAction(actionState))
        }
    }}