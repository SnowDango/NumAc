package com.snowdango.numac.store.appview

import androidx.lifecycle.*
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseAction
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionState
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
import com.snowdango.numac.actions.visible.ToggleVisibleAction
import com.snowdango.numac.actions.visible.ToggleVisibleActionState
import com.snowdango.numac.dispatcher.Dispatcher
import kotlinx.coroutines.CoroutineScope

class AppViewStore(private val dispatcher: Dispatcher):
        ViewModel(), Dispatcher.DatabaseActionListener, Dispatcher.RecentlyActionListener
        ,Dispatcher.RemoveAppActionListener,Dispatcher.ChangeCommandActionListener,
        Dispatcher.ControlFavoriteActionListener, Dispatcher.ControlVisibleActionListener,
        Dispatcher.AppInvisibleListListener{

    init {
        dispatcher.registerAppView(this,this,
                this,this,
                this,this,this)
    }

    val viewModelCoroutineScope: CoroutineScope = viewModelScope

    val databaseActionData: LiveData<DatabaseActionState> = MutableLiveData<DatabaseActionState>().apply { value = DatabaseActionState.None }
    val invisibleAppActionData: LiveData<AppInvisibleListDatabaseActionState> = MutableLiveData<AppInvisibleListDatabaseActionState>().apply { value = AppInvisibleListDatabaseActionState.None }
    val recentlyActionData: LiveData<RecentlyAppDatabaseActionState> = MutableLiveData<RecentlyAppDatabaseActionState>().apply { value = RecentlyAppDatabaseActionState.None }
    val removeActionData: LiveData<RemoveAppActionState> = MutableLiveData<RemoveAppActionState>().apply { value = RemoveAppActionState.None }
    val changeCommandData: LiveData<ChangeCommandActionState> = MutableLiveData<ChangeCommandActionState>().apply { value = ChangeCommandActionState.None }
    val controlFavoriteData: LiveData<ControlFavoriteActionState> = MutableLiveData<ControlFavoriteActionState>().apply { value = ControlFavoriteActionState.None }
    val controlVisibleData: LiveData<ToggleVisibleActionState> = MutableLiveData<ToggleVisibleActionState>().apply { value = ToggleVisibleActionState.None }


    private fun updateDatabase(action: DatabaseAction) {
        (databaseActionData as MutableLiveData<DatabaseActionState>).value = action.state
    }

    private fun updateAppInvisible(action: AppInvisibleListDatabaseAction){
        (invisibleAppActionData as MutableLiveData<AppInvisibleListDatabaseActionState>).value = action.state
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

    private fun updateControlVisible(action: ToggleVisibleAction){
        (controlVisibleData as MutableLiveData<ToggleVisibleActionState>).value = action.state
    }

    override fun on(action: RecentlyAppDatabaseAction) = updateRecently(action)

    override fun on(action: AppInvisibleListDatabaseAction) = updateAppInvisible(action)

    override fun on(action: DatabaseAction) = updateDatabase(action)

    override fun on(action: RemoveAppAction) = updateRemoveApp(action)

    override fun on(action: ChangeCommandAction) = updateChangeCommand(action)

    override fun on(action: ControlFavoriteAction) = updateControlFavorite(action)

    override fun on(action: ToggleVisibleAction) = updateControlVisible(action)

    override fun onCleared() {
        super.onCleared()
        dispatcher.unregisterAppView(
                this,this,
                this,this,
                this,this,this)
    }
}