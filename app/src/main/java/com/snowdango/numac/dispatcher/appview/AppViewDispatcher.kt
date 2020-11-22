package com.snowdango.numac.dispatcher.appview

import android.util.Log
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.database.DatabaseAction
import com.snowdango.numac.dispatcher.main.MainDispatcher
import java.util.*

class AppViewDispatcher {

    private val databaseListeners = Collections.synchronizedList(mutableListOf<DatabaseActionListener>())

    interface DatabaseActionListener {
        fun on(action: DatabaseAction)
    }

    fun dispatchDatabase(action: DatabaseAction) {
        databaseListeners.forEach { it.on(action) }
    }

    fun register(listener: DatabaseActionListener) {
        databaseListeners.add(listener)
    }

    fun unregister(listener: DatabaseActionListener) {
        databaseListeners.remove(listener)
    }
}