package com.snowdango.numac.actions.controlfavorite

import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.AppListDatabaseUse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ControlFavoriteActionCreator(private val coroutineScope: CoroutineScope,
                                   private val dispatcher: Dispatcher,
                                   private val appListDatabaseUse: AppListDatabaseUse) {


    fun execute(packageName: String,favorite: Int){
        coroutineScope.launch(Dispatchers.IO){
            favoriteControlActionCreate(packageName,favorite)
        }
    }

    private suspend fun favoriteControlActionCreate(packageName: String,favorite: Int){
        val action = when(appListDatabaseUse.updateFavorite(packageName, favorite)){
            is AppListDatabaseUse.DatabaseResult.Success -> ControlFavoriteAction(ControlFavoriteActionState.Success)
            is AppListDatabaseUse.DatabaseResult.Failed -> ControlFavoriteAction(ControlFavoriteActionState.Failed)
        }
        coroutineScope.launch(Dispatchers.Main){
            dispatcher.dispatchFavoriteControl(action)
        }
    }
}