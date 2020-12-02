package com.snowdango.numac.actions.apprecently

import android.util.Log
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.SaveRecentlyApp
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyAppDatabaseActionCreator(
        private val coroutineScope: CancellableCoroutineScope,
        private val dispatcher: Dispatcher,
        private val saveRecentlyApp: SaveRecentlyApp
) {

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
                when(val result = saveRecentlyApp.updateRecentlyAppList(packageName)){
                    is SaveRecentlyApp.SaveRecentlyAppResult.Success ->  RecentlyAppDatabaseActionState.Success(result.recentlyAppList)
                    is SaveRecentlyApp.SaveRecentlyAppResult.Failed -> RecentlyAppDatabaseActionState.Failed
                }

        coroutineScope.launch(Dispatchers.Main) {
            dispatcher.dispatchRecently(RecentlyAppDatabaseAction(actionState))
        }
    }

    private suspend fun getRecentlyApp(){
        val actionState =
                when(val result = saveRecentlyApp.getRecentlyAppList()){
                    is SaveRecentlyApp.SaveRecentlyAppResult.Success -> RecentlyAppDatabaseActionState.Success(result.recentlyAppList)
                    is SaveRecentlyApp.SaveRecentlyAppResult.Failed -> RecentlyAppDatabaseActionState.Failed
                }
        Log.d("getRecently",actionState.toString())
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchRecently(RecentlyAppDatabaseAction(actionState))
        }
    }}