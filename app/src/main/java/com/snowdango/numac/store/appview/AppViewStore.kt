package com.snowdango.numac.store.appview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.snowdango.numac.actions.database.DatabaseAction
import com.snowdango.numac.actions.database.DatabaseActionState
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.dispatcher.appview.AppViewDispatcher

class AppViewStore(private val appViewDispatcher: AppViewDispatcher): ViewModel(), AppViewDispatcher.DatabaseActionListener {

    init {
        appViewDispatcher.register(this)
    }

    val databaseActionData: LiveData<DatabaseActionState> = MutableLiveData<DatabaseActionState>().apply { value = DatabaseActionState.None }
    var appList: ArrayList<AppInfo> = ArrayList()

    private fun updateDatabase(action: DatabaseAction) {
        (databaseActionData as MutableLiveData<DatabaseActionState>).value = action.state
        if(action.state is DatabaseActionState.Success){
            appList = action.state.appList
        }
    }

    override fun on(action: DatabaseAction) = updateDatabase(action)

    override fun onCleared() {
        super.onCleared()
        appViewDispatcher.unregister(this)
    }



}