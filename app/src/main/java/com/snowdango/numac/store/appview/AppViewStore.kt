package com.snowdango.numac.store.appview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.numac.actions.applistdb.DatabaseAction
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseAction
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.changecommnad.ChangeCommandAction
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionState
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteAction
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionState
import com.snowdango.numac.actions.removeapp.RemoveAppAction
import com.snowdango.numac.actions.removeapp.RemoveAppActionState
import com.snowdango.numac.dispatcher.Dispatcher
import kotlinx.coroutines.CoroutineScope

class AppViewStore(private val dispatcher: Dispatcher):
        ViewModel(), Dispatcher.DatabaseActionListener, Dispatcher.RecentlyActionListener
        ,Dispatcher.RemoveAppActionListener,Dispatcher.ChangeCommandActionListener,
        Dispatcher.ControlFavoriteActionListener{

    init {
        dispatcher.registerAppView(this,this,this,this,this)
    }

    val viewModelCoroutineScope: CoroutineScope = viewModelScope

    val databaseActionData: LiveData<DatabaseActionState> = MutableLiveData<DatabaseActionState>().apply { value = DatabaseActionState.None }
    val recentlyActionData: LiveData<RecentlyAppDatabaseActionState> = MutableLiveData<RecentlyAppDatabaseActionState>().apply { value = RecentlyAppDatabaseActionState.None }
    val removeActionData: LiveData<RemoveAppActionState> = MutableLiveData<RemoveAppActionState>().apply { value = RemoveAppActionState.None }
    val changeCommandData: LiveData<ChangeCommandActionState> = MutableLiveData<ChangeCommandActionState>().apply { value = ChangeCommandActionState.None }
    val controlFavoriteData: LiveData<ControlFavoriteActionState> = MutableLiveData<ControlFavoriteActionState>().apply { value = ControlFavoriteActionState.None }


    private fun updateDatabase(action: DatabaseAction) {
        (databaseActionData as MutableLiveData<DatabaseActionState>).value = action.state
    }

    private fun updateRecently(action: RecentlyAppDatabaseAction) {
        (recentlyActionData as MutableLiveData<RecentlyAppDatabaseActionState>).value = action.state
    }

    private fun updateRemoveApp(action: RemoveAppAction){
        (removeActionData as MutableLiveData<RemoveAppActionState>).value = action.state
    }

    private fun updateChangeCommand(action: ChangeCommandAction){
        (changeCommandData as MutableLiveData<ChangeCommandActionState>).value = action.state
    }

    private fun updateControlFavorite(action: ControlFavoriteAction){
        (controlFavoriteData as MutableLiveData<ControlFavoriteActionState>).value = action.state
    }

    override fun on(action: RecentlyAppDatabaseAction) = updateRecently(action)

    override fun on(action: DatabaseAction) = updateDatabase(action)

    override fun on(action: RemoveAppAction) = updateRemoveApp(action)

    override fun on(action: ChangeCommandAction) = updateChangeCommand(action)

    override fun on(action: ControlFavoriteAction) = updateControlFavorite(action)

    override fun onCleared() {
        super.onCleared()
        dispatcher.unregisterAppView(this,this,this,this,this)
    }

}